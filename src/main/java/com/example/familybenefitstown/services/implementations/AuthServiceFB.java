package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.api_models.auth.LoginResponse;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.security.services.interfaces.TokenCodeService;
import com.example.familybenefitstown.services.interfaces.AuthService;
import com.example.familybenefitstown.services.interfaces.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, отвечающего за аутентификацию и авторизацию в системе
 */
@Service
public class AuthServiceFB implements AuthService {

  /**
   * Репозиторий, работающий с моделью таблицы "user"
   */
  private final UserRepository userRepository;

  /**
   * Сервис для работы с токенами доступа (в формате jwt) и восстановления и кодом для входа
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
   * Конструктор для инициализации интерфейсов репозиториев и сервисов
   * @param userRepository репозиторий, работающий с моделью таблицы "user"
   * @param tokenCodeService интерфейс сервиса для работы с токеном доступа (в формате jwt) и кодом для входа
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param mailService интерфейс сервиса для отправки сообщений на электронную почту
   */
  @Autowired
  public AuthServiceFB(UserRepository userRepository,
                       TokenCodeService tokenCodeService,
                       DBIntegrityService dbIntegrityService,
                       MailService mailService) {
    this.userRepository = userRepository;
    this.tokenCodeService = tokenCodeService;
    this.dbIntegrityService = dbIntegrityService;
    this.mailService = mailService;
  }

  /**
   * Отправляет на почту пользователю код для входа в систему
   * @param email почта пользователя
   * @throws NotFoundException если не найден пользователь по указанным данным
   * @throws MailException если не удалось отправить сообщение
   */
  @Override
  public void preLogin(String email) throws NotFoundException, MailException {

    // Получение пользователя по его email, если пользователь существует
    String preparedEmail = dbIntegrityService.preparePostgreSQLString(email);
    UserEntity userEntityFromRequest = userRepository.findByEmail(preparedEmail)
        .orElseThrow(() -> new NotFoundException(String.format(
            "User with email \"%s\" not found", email)));

    // Получение сгенерированного кода для входа
    int code = tokenCodeService.generateAndSaveLoginCode(userEntityFromRequest.getId());

    // Отправка кода на почту
    mailService.sendLoginCode(email, userEntityFromRequest.getName(), code);
  }

  /**
   * Вход в систему по почте и коду для входа
   * @param email почта пользователя
   * @param loginCode код для входа пользователя
   * @return объект ответа на вход в систему
   */
  @Override
  public LoginResponse login(String email, int loginCode) {

    // Получение пользователя по его email, если пользователь существует
    String preparedEmail = dbIntegrityService.preparePostgreSQLString(email);
    UserEntity userEntityFromRequest = userRepository.getByEmail(preparedEmail);

    String idUser = userEntityFromRequest.getId();

    // Удаление кода входа
    tokenCodeService.removeLoginCodeByIdUser(idUser);

    // Формирование ответа
    return LoginResponse
        .builder()
        .idUser(idUser)
        .nameUser(userEntityFromRequest.getName())
        .nameRoleUserSet(userEntityFromRequest.getRoleEntityList()
                             .stream()
                             .map(RoleEntity::getName)
                             .collect(Collectors.toSet()))
        .build();
  }

  /**
   * Выход из системы
   * @param request http запрос
   */
  @Override
  public void logout(HttpServletRequest request) {

    String refreshToken = tokenCodeService.refreshTokenFromRequest(request);
    tokenCodeService.removeRefreshToken(refreshToken);
  }
}
