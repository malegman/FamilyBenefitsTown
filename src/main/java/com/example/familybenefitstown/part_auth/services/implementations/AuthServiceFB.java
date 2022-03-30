package com.example.familybenefitstown.part_auth.services.implementations;

import com.example.familybenefitstown.dto.repositories.LoginCodeRepository;
import com.example.familybenefitstown.dto.repositories.RefreshTokenRepository;
import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.part_auth.models.*;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.dto.repositories.RoleRepository;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_auth.MailSenderProvider;
import com.example.familybenefitstown.part_auth.services.interfaces.TokenCodeService;
import com.example.familybenefitstown.part_auth.services.interfaces.AuthService;
import com.example.familybenefitstown.part_auth.HttpHeadersSupport;
import com.example.familybenefitstown.security.DBSecuritySupport;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, отвечающего за аутентификацию и авторизацию в системе
 */
@Slf4j
@Service
public class AuthServiceFB implements AuthService {

  /**
   * Репозиторий, работающий с моделью таблицы "user"
   */
  private final UserRepository userRepository;

  /**
   * Репозиторий, работающий с моделью таблицы "role"
   */
  private final RoleRepository roleRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "refresh_token"
   */
  private final RefreshTokenRepository refreshTokenRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "login_code"
   */
  private final LoginCodeRepository loginCodeRepository;

  /**
   * Сервис для работы с токенами доступа (в формате jwt) и восстановления и кодом для входа
   */
  private final TokenCodeService tokenCodeService;

  /**
   * Конструктор для инициализации интерфейсов репозиториев и сервисов
   * @param userRepository репозиторий, работающий с моделью таблицы "user"
   * @param roleRepository репозиторий, работающий с моделью таблицы "role"
   * @param refreshTokenRepository репозиторий, работающий с моделью таблицы "refresh_token"
   * @param loginCodeRepository репозиторий, работающий с моделью таблицы "login_code"
   * @param tokenCodeService интерфейс сервиса для работы с токеном доступа (в формате jwt) и кодом для входа
   */
  @Autowired
  public AuthServiceFB(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       LoginCodeRepository loginCodeRepository,
                       TokenCodeService tokenCodeService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.loginCodeRepository = loginCodeRepository;
    this.tokenCodeService = tokenCodeService;
  }

  /**
   * Отправляет на почту пользователю код для входа в систему
   * @param email почта пользователя
   * @throws NotFoundException если пользователь с данным email не найден
   * @throws MailException если не удалось отправить сообщение
   */
  @Override
  public void preLogin(String email) throws NotFoundException, MailException {

    // Получение пользователя по его email, если пользователь существует
    String preparedEmail = DBSecuritySupport.preparePostgreSQLString(email);
    UserEntity userEntityFromRequest = userRepository.findByEmail(preparedEmail).orElseThrow(
        () -> new NotFoundException(String.format("User with email \"%s\" not found", email)));

    // Получение сгенерированного кода для входа
    int code = tokenCodeService.generateAndSaveLoginCode(userEntityFromRequest.getId());

    // Отправка кода на почту
    MailSenderProvider.sendLoginCode(email, userEntityFromRequest.getName(), code);
  }

  /**
   * Вход в систему по почте и коду для входа
   * @param email почта пользователя
   * @param loginCode код для входа пользователя
   * @return объект ответа на вход в систему
   * @throws NotFoundException если не найден пользователь по указанным данным
   * @throws DateTimeException если полученный код входа истек
   */
  @Override
  public LoginResponse login(String email, int loginCode) throws NotFoundException, DateTimeException {

    // Получение пользователя по его email, если пользователь существует
    String preparedEmail = DBSecuritySupport.preparePostgreSQLString(email);
    UserEntity userEntityFromRequest = userRepository.findByEmail(preparedEmail).orElseThrow(
        () -> new NotFoundException(String.format("User with email \"%s\" not found", email)));

    String idUser = tokenCodeService.checkLoginCode(loginCode);

    // Удаление кода входа
    loginCodeRepository.deleteByCode(loginCode);

    // Формирование ответа
    return LoginResponse
        .builder()
        .idUser(idUser)
        .nameUser(userEntityFromRequest.getName())
        .nameRoleUserList(roleRepository.findAllByIdUser(idUser)
                             .stream()
                             .map(RoleEntity::getName)
                             .collect(Collectors.toList()))
        .build();
  }

  /**
   * Выход из системы. Удаляет токен восстановления авторизованного пользователя
   * @param idUser ID существующего пользователя, запрашивающего выход
   */
  @Override
  public void logout(String idUser) {

    refreshTokenRepository.deleteById(idUser);
  }

  /**
   * Проверяет запрос на аутентификацию.
   * <ol>
   *   <li>
   *     При успешной аутентификации, возвращает объект с данными пользователя, извлеченными из jwt, и неизмененным http ответом.
   *   </li>
   *   <li>
   *     Если токен восстановления корректный и jwt валидный, но истекший, создаются и сохраняются новые токены.
   *     Возвращаются данные пользователя и http ответ с обновленными токенами.
   *   </li>
   * </ol>
   * @param request http запрос, который необходимо проверить
   * @param response http ответ
   * @return Объект с данными пользователя.
   * Возвращается {@code empty}, если токен восстановления истек или не был найден или не удалось обработать jwt
   */
  @Override
  public Optional<JwtUserData> authenticate(HttpServletRequest request, HttpServletResponse response) {

    String requestURI = request.getRequestURI();
    String requestMethod = request.getMethod();
    String requestAddress = request.getRemoteAddr();

    String requestJwt = HttpHeadersSupport.getJwt(request);
    String requestRefreshToken = HttpHeadersSupport.getRefreshToken(request);

    // Проверка токена восстановления
    String idUser;
    try {
      idUser = tokenCodeService.checkRefreshToken(requestRefreshToken);
    } catch (NotFoundException | DateTimeException e) {
      log.warn("{} {} \"{}\": Refresh token's exceptions. {}", requestAddress, requestMethod, requestURI, e.getMessage());
      return Optional.empty();
    }

    JwtUserData userData;
    // Проверка токена jwt
    try {
      userData = tokenCodeService.checkJwt(requestJwt);

    } catch (ExpiredJwtException e) {
      // Токен jwt истек, но корректный.
      // Запрос новых токенов и сохранение в бд токена восстановления
      AuthData newAuthData = tokenCodeService.generateAndSaveAuthTokens(idUser);
      userData = newAuthData.getJwtData().getUserData();
      // Установка токенов в заголовки http ответа
      HttpHeadersSupport.setTokens(response, newAuthData);

    } catch (RuntimeException e) {
      // Токен jwt некорректный.
      // Удаление токена восстановления.
      HttpHeadersSupport.removeRefreshToken(response);
      refreshTokenRepository.deleteByToken(requestRefreshToken);
      log.warn("{} {} \"{}\": Jwt token's exceptions. {}", requestAddress, requestMethod, requestURI, e.getMessage());
      return Optional.empty();
    }

    return Optional.of(userData);
  }
}
