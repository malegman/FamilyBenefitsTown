package com.example.familybenefitstown.security.services.implementations;

import com.example.familybenefitstown.dto.entities.LoginCodeEntity;
import com.example.familybenefitstown.dto.entities.RefreshTokenEntity;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.repositories.LoginCodeRepository;
import com.example.familybenefitstown.dto.repositories.RefreshTokenRepository;
import com.example.familybenefitstown.dto.repositories.RoleRepository;
import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.security.generator.RandomValue;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.security.services.interfaces.TokenCodeService;
import com.example.familybenefitstown.security.web.auth.AuthTokens;
import com.example.familybenefitstown.security.web.auth.JwtUserData;
import com.example.familybenefitstown.services.interfaces.DateTimeService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с токенами доступа (в формате jwt) и восстановления и кодом для входа
 */
@Slf4j
@Service
public class TokenCodeServiceFB implements TokenCodeService {

  /**
   * Репозиторий, работающий с моделью таблицы "access_token"
   */
  private final RefreshTokenRepository refreshTokenRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "login_code"
   */
  private final LoginCodeRepository loginCodeRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "role"
   */
  private final RoleRepository roleRepository;

  /**
   * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  private final DateTimeService dateTimeService;
  /**
   * Интерфейс сервиса, отвечающего за целостность базы данных
   */
  private final DBIntegrityService dbIntegrityService;

  /**
   * Конструктор для инициализации сервиса
   * @param refreshTokenRepository репозиторий, работающий с моделью таблицы "access_token"
   * @param loginCodeRepository репозиторий, работающий с моделью таблицы "login_code"
   * @param roleRepository репозиторий, работающий с моделью таблицы "role"
   * @param dateTimeService интерфейс сервиса, который предоставляет методы для работы с датой и временем
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   */
  @Autowired
  public TokenCodeServiceFB(RefreshTokenRepository refreshTokenRepository,
                            LoginCodeRepository loginCodeRepository,
                            RoleRepository roleRepository,
                            DateTimeService dateTimeService, DBIntegrityService dbIntegrityService) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.loginCodeRepository = loginCodeRepository;
    this.roleRepository = roleRepository;
    this.dateTimeService = dateTimeService;
    this.dbIntegrityService = dbIntegrityService;
  }

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param id ID пользователя
   * @param roleEntityList список ролей пользователя
   * @return сгенерированный jwt
   */
  @Override
  public String generateJwt(String id, List<RoleEntity> roleEntityList) {

    return generateJwt(JwtUserData
                           .builder()
                           .idUser(id)
                           .nameRoleSet(roleEntityList
                                            .stream()
                                            .map(RoleEntity::getName)
                                            .collect(Collectors.toSet()))
                           .build());
  }

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param userData данные доступа из токена доступа jwt
   * @return сгенерированный jwt
   */
  @Override
  public String generateJwt(JwtUserData userData) {

    return Jwts.builder()
        .setSubject(userData.toString())
        .setExpiration(Date.from(dateTimeService.getExpiration(R.JWT_EXPIRATION_SEC).toInstant(ZoneOffset.UTC)))
        .signWith(SignatureAlgorithm.HS512, R.JWT_SECRET)
        .compact();
  }

  /**
   * Извлекает данные пользователя из строки, формата токена jwt
   * @param jwt токен пользователя, jwt
   * @return данные пользователя
   * @throws RuntimeException если не удалось извлечь данные пользователя из строки
   */
  @Override
  public JwtUserData dataFromJwt(String jwt) throws RuntimeException {

    return JwtUserData.fromString(
        Jwts.parser().setSigningKey(R.JWT_SECRET)
            .parseClaimsJws(jwt).getBody().getSubject());
  }

  /**
   * Генерирует и сохраняет токен восстановления указанной длины из символов A-Za-z0-9 для указанного пользователя
   * @param idUser ID пользователя
   * @return сгенерированный токен восстановления
   */
  @Override
  public String generateAndSaveRefreshToken(String idUser) {

    String refreshToken = RandomValue.randomString(R.REFRESH_LENGTH);

    // Сохранение токена восстановления
    refreshTokenRepository.saveAndFlush(RefreshTokenEntity
                                            .builder()
                                            .idUser(idUser)
                                            .token(refreshToken)
                                            .dateExpiration(dateTimeService.getExpiration(R.REFRESH_EXPIRATION_SEC))
                                            .build());
    log.info("DB. Refresh token \"{}\" created for the user with id \"{}\"", refreshToken, idUser);

    return refreshToken;
  }

  /**
   * Возвращает ID пользователя, который владеет указанным токеном восстановления
   * @param refreshToken токена восстановления пользователя
   * @return {@code true}, если токен восстановления пользователя есть в бд
   * @throws NotFoundException если токен восстановления не найден
   * @throws DateTimeException если полученный токен восстановления истек
   */
  @Override
  public String getIdOfNotExpiredRefreshToken(String refreshToken) throws NotFoundException, DateTimeException {

    // Получение модели токена восстановления из бд
    String prepareRefreshToken = dbIntegrityService.preparePostgreSQLString(refreshToken);
    RefreshTokenEntity refreshTokenEntityFromRequest = refreshTokenRepository.findByToken(prepareRefreshToken)
        .orElseThrow(() -> new NotFoundException(String.format(
            "Refresh token %s not found", refreshToken)));

    // Проверка токена восстановления на свежесть
    dateTimeService.checkDateTimeAfterNow(refreshTokenEntityFromRequest.getDateExpiration());

    return refreshTokenEntityFromRequest.getIdUser();
  }

  /**
   * Генерирует и сохраняет код для входа в систему для указанного пользователя
   * @param idUser ID пользователя
   * @return сгенерированный код
   */
  @Override
  public int generateAndSaveLoginCode(String idUser) {

    int loginCode = RandomValue.randomInteger(R.LOGIN_CODE_LENGTH);

    // Сохранение кода в бд
    loginCodeRepository.saveAndFlush(LoginCodeEntity.builder()
                                         .idUser(idUser)
                                         .code(loginCode)
                                         .dateExpiration(dateTimeService.getExpiration(R.LOGIN_EXPIRATION_SEC))
                                         .build());
    log.info("DB. Login code \"{}\" created for the user with id \"{}\"", loginCode, idUser);

    return loginCode;
  }

  /**
   * Извлекает токен доступа jwt из запроса клиента. {@code null}, если токен не найден
   * @param request запрос клиента
   * @return токен доступа пользователя.
   */
  @Override
  public String jwtFromRequest(HttpServletRequest request) {

    if (request == null) {
      return null;
    }

    String bearer = request.getHeader(R.AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearer) && bearer.startsWith(R.ACCESS_TOKEN_PREFIX)) {
      return bearer.substring(R.ACCESS_TOKEN_PREFIX.length());
    }

    return null;
  }

  /**
   * Извлекает токен восстановления из запроса клиента. {@code null}, если токен не найден
   * @param request запрос клиента
   * @return токен восстановления пользователя или null, если токен не найден
   */
  @Override
  public String refreshTokenFromRequest(HttpServletRequest request) {

    if (request == null) {
      return null;
    }

    try {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName().equals(R.REFRESH_NAME_COOKIE)) {
          return cookie.getValue();
        }
      }
    } catch (RuntimeException e) {
      return null;
    }

    return null;
  }

  /**
   * Генерирует новые токены доступа (jwt) и восстановления по коду входа пользователя, полученного по почте.
   * Сгенерированный токен восстановления сохраняется в бд.
   * @param emailLoginCode код входа пользователя
   * @return контейнер с токенами доступа (jwt) и восстановления
   * @throws NotFoundException если код входа не найден
   * @throws DateTimeException если полученный код для входа истек
   */
  @Override
  public AuthTokens generateAuthTokens(int emailLoginCode) throws NotFoundException, DateTimeException {

    // Получение модели токена восстановления из бд
    LoginCodeEntity loginCodeEntityFromRequest = loginCodeRepository.findByCode(emailLoginCode)
        .orElseThrow(() -> new NotFoundException(String.format(
            "Login code %s not found", emailLoginCode)));

    // Проверка кода на свежесть
    dateTimeService.checkDateTimeAfterNow(loginCodeEntityFromRequest.getDateExpiration());

    return generateAuthTokens(loginCodeEntityFromRequest.getIdUser());
  }

  /**
   * Генерирует новые токены доступа (jwt) и восстановления по ID пользователя.
   * Сгенерированный токен восстановления сохраняется в бд.
   * @param idUser ID пользователя
   * @return контейнер с токенами доступа (jwt) и восстановления
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  @Override
  public AuthTokens generateAuthTokens(String idUser) throws NotFoundException {

    // Проверка существования пользователя по его ролям
    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    List<RoleEntity> roleEntitySet = roleRepository.findAllByIdUser(prepareIdUser);
    if (roleEntitySet.isEmpty()) {
      throw new NotFoundException(String.format(
          "User with ID \"%s\" not found", idUser));
    }

    // Генерация токенов
    String newJwt = generateJwt(idUser, roleEntitySet);
    String newRefreshToken = generateAndSaveRefreshToken(idUser);

    return AuthTokens
        .builder()
        .jwt(newJwt)
        .refreshToken(newRefreshToken)
        .build();
  }

  /**
   * Удаляет токен восстановления по ID пользователя
   * @param refreshToken токен восстановления пользователя
   */
  @Override
  public void removeRefreshToken(String refreshToken) {

    String prepareRefreshToken = dbIntegrityService.preparePostgreSQLString(refreshToken);
    refreshTokenRepository.deleteByToken(prepareRefreshToken);
    log.info("DB. Refresh token \"{}\" deleted.", refreshToken);
  }

  /**
   * Удаляет код входа по ID пользователя
   * @param idUser ID пользователя
   */
  @Override
  public void removeLoginCodeByIdUser(String idUser) {

    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    loginCodeRepository.deleteById(prepareIdUser);
    log.info("DB. User login code with id \"{}\" deleted.", idUser);
  }

  /**
   * Удаляет токен восстановления и код входа по ID пользователя
   * @param idUser ID пользователя
   */
  @Override
  public void removeTokenCodeByIdUser(String idUser) {

    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    refreshTokenRepository.deleteById(prepareIdUser);
    log.info("DB. User refresh token with id \"{}\" deleted.", idUser);
    loginCodeRepository.deleteById(prepareIdUser);
    log.info("DB. User login code with id \"{}\" deleted.", idUser);
  }
}
