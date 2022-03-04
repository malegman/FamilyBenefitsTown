package com.example.familybenefitstown.service.impl;

import com.example.familybenefitstown.api_model.system.LoginRequest;
import com.example.familybenefitstown.api_model.system.LoginResponse;
import com.example.familybenefitstown.api_model.system.PreLoginRequest;
import com.example.familybenefitstown.dto.entity.AccessTokenEntity;
import com.example.familybenefitstown.dto.entity.LoginCodeEntity;
import com.example.familybenefitstown.dto.entity.RoleEntity;
import com.example.familybenefitstown.dto.entity.UserEntity;
import com.example.familybenefitstown.dto.repository.AccessTokenRepository;
import com.example.familybenefitstown.dto.repository.LoginCodeRepository;
import com.example.familybenefitstown.dto.repository.UserRepository;
import com.example.familybenefitstown.exception.NotFoundException;
import com.example.familybenefitstown.resource.R;
import com.example.familybenefitstown.security.service.inface.DBIntegrityService;
import com.example.familybenefitstown.security.service.inface.TokenCodeService;
import com.example.familybenefitstown.security.web.auth.JwtAuthenticationUserData;
import com.example.familybenefitstown.service.inface.DateTimeService;
import com.example.familybenefitstown.service.inface.MailService;
import com.example.familybenefitstown.service.inface.SystemService;
import com.example.familybenefitstown.service.model.ServiceLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, отвечающего за системные функции
 */
@Service
public class SystemServiceFB implements SystemService {

  /**
   * Репозиторий, работающий с моделью таблицы "user"
   */
  private final UserRepository userRepository;

  /**
   * Репозиторий, работающий с моделью таблицы "access_token"
   */
  private final AccessTokenRepository accessTokenRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "login_code"
   */
  private final LoginCodeRepository loginCodeRepository;

  /**
   * Интерфейс сервиса для работы с токеном доступа (в формате jwt) и кодом для входа
   */
  private final TokenCodeService tokenCodeService;

  /**
   * Интерфейс сервиса, отвечающего за целостность базы данных
   */
  private final DBIntegrityService dbIntegrityService;

  /**
   * Интерфейс сервиса для отправки сообщений на электронную почту
   */
  private final MailService mailService;

  /**
   * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  private final DateTimeService dateTimeService;

  /**
   * Конструктор для инициализации интерфейсов репозиториев и сервисов
   * @param userRepository репозиторий, работающий с моделью таблицы "user"
   * @param accessTokenRepository репозиторий, работающий с моделью таблицы "access_token"
   * @param loginCodeRepository репозиторий, работающий с моделью таблицы "login_code"
   * @param tokenCodeService интерфейс сервиса для работы с токеном доступа (в формате jwt) и кодом для входа
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param mailService интерфейс сервиса для отправки сообщений на электронную почту
   * @param dateTimeService интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  @Autowired
  public SystemServiceFB(UserRepository userRepository,
                         AccessTokenRepository accessTokenRepository,
                         LoginCodeRepository loginCodeRepository,
                         TokenCodeService tokenCodeService,
                         DBIntegrityService dbIntegrityService,
                         MailService mailService,
                         DateTimeService dateTimeService) {
    this.userRepository = userRepository;
    this.accessTokenRepository = accessTokenRepository;
    this.loginCodeRepository = loginCodeRepository;
    this.tokenCodeService = tokenCodeService;
    this.dbIntegrityService = dbIntegrityService;
    this.mailService = mailService;
    this.dateTimeService = dateTimeService;
  }

  /**
   * Отправляет на почту пользователю код для входа в систему
   * @param preLoginRequest объект запроса пользователя для получения кода для входа в систему
   * @throws NotFoundException если не найден пользователь по указанным данным
   * @throws MailException если не удалось отправить сообщение
   */
  @Override
  public void preLogin(PreLoginRequest preLoginRequest) throws NotFoundException, MailException {

    String email = preLoginRequest.getEmail();
    String preparedEmail = dbIntegrityService.preparePostgreSQLString(email);

    // Получение пользователя по его email, если пользователь существует
    UserEntity userEntityFromRequest = userRepository.findByEmail(preparedEmail)
        .orElseThrow(() -> new NotFoundException(String.format(
            "User with email \"%s\" not found", email)));

    // Получение сгенерированного кода для входа
    int code = tokenCodeService.generateLoginCode();

    // Отправка кода на почту
    mailService.sendLoginCode(email, userEntityFromRequest.getName(), code);

    // Сохранение кода в бд
    loginCodeRepository.saveAndFlush(LoginCodeEntity.builder()
                                         .idUser(userEntityFromRequest.getId())
                                         .code(BigInteger.valueOf(code))
                                         .dateExpiration(dateTimeService.getExpiration(R.LOGIN_EXPIRATION_SEC))
                                         .build());
  }

  /**
   * Вход в систему
   * @param loginRequest объект запроса пользователя для входа в систему
   * @param request http запрос
   * @return объект ответа на вход в систему
   * @throws NotFoundException если не найден пользователь по указанным данным
   */
  @Override
  public ServiceLoginResponse login(LoginRequest loginRequest, HttpServletRequest request) throws NotFoundException {

    String email = loginRequest.getEmail();

    String preparedEmail = dbIntegrityService.preparePostgreSQLString(email);

    // Получение пользователя по его email, если пользователь существует
    UserEntity userEntityFromRequest = userRepository.findByEmail(preparedEmail)
        .orElseThrow(() -> new NotFoundException(String.format(
            "User with email \"%s\" not found", email)));

    String idUser = userEntityFromRequest.getId();

    // Создание jwt
    String jwt = tokenCodeService.generateJwt(idUser, userEntityFromRequest.getRoleEntitySet(), request);

    // Сохранение токена
    accessTokenRepository.saveAndFlush(AccessTokenEntity
                                           .builder()
                                           .idUser(idUser)
                                           .token(jwt)
                                           .build());

    // Формирование ответа
    return ServiceLoginResponse
        .builder()
        .loginResponse(LoginResponse
                           .builder()
                           .idUser(idUser)
                           .nameUser(userEntityFromRequest.getName())
                           .nameRoleUserSet(userEntityFromRequest.getRoleEntitySet()
                                                .stream()
                                                .map(RoleEntity::getName)
                                                .collect(Collectors.toSet()))
                           .build())
        .jwt(jwt)
        .build();
  }

  /**
   * Выход из системы
   * @param userAuth данные доступа из токена доступа jwt
   * @throws NotFoundException если не найден пользователь по указанным данным
   */
  @Override
  public void logout(JwtAuthenticationUserData userAuth) throws NotFoundException {

    String idUser = userAuth.getIdUser();

    // Проверка существования токена пользователя
    dbIntegrityService.checkExistenceById(
        accessTokenRepository::existsById, idUser);

    accessTokenRepository.deleteById(idUser);
  }

  /**
   * Обновляет токен доступа пользователя
   * @param userAuth данные доступа из токена доступа jwt
   * @return новый токен доступа jwt
   * @throws NotFoundException если не найден пользователь по указанным данным
   */
  @Override
  public String refresh(JwtAuthenticationUserData userAuth) throws NotFoundException {

    String idUser = userAuth.getIdUser();

    // Проверка существования токена пользователя
    dbIntegrityService.checkExistenceById(
        accessTokenRepository::existsById, idUser);

    // Создание jwt
    String jwt = tokenCodeService.generateJwt(userAuth);

    // Сохранение токена
    accessTokenRepository.saveAndFlush(AccessTokenEntity
                                           .builder()
                                           .idUser(idUser)
                                           .token(jwt)
                                           .build());

    return jwt;
  }
}
