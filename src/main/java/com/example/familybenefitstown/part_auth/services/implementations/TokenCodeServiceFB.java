package com.example.familybenefitstown.part_auth.services.implementations;

import com.example.familybenefitstown.dto.entities.LoginCodeEntity;
import com.example.familybenefitstown.dto.entities.RefreshTokenEntity;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.repositories.LoginCodeRepository;
import com.example.familybenefitstown.dto.repositories.RefreshTokenRepository;
import com.example.familybenefitstown.dto.repositories.RoleRepository;
import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_auth.models.AuthData;
import com.example.familybenefitstown.part_auth.models.JwtData;
import com.example.familybenefitstown.part_auth.models.JwtUserData;
import com.example.familybenefitstown.part_auth.services.interfaces.TokenCodeService;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.security.RandomValue;
import com.example.familybenefitstown.security.DBSecuritySupport;
import com.example.familybenefitstown.security.DateTimeSupport;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
   * Конструктор для инициализации сервиса
   * @param refreshTokenRepository репозиторий, работающий с моделью таблицы "access_token"
   * @param roleRepository репозиторий, работающий с моделью таблицы "role"
   */
  @Autowired
  public TokenCodeServiceFB(RefreshTokenRepository refreshTokenRepository,
                            LoginCodeRepository loginCodeRepository,
                            RoleRepository roleRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.loginCodeRepository = loginCodeRepository;
    this.roleRepository = roleRepository;
  }

  /**
   * Извлекает данные пользователя из строки, формата токена jwt
   * @param jwt токен пользователя, jwt
   * @return данные пользователя
   * @throws RuntimeException если не удалось извлечь данные пользователя из строки
   */
  @Override
  public JwtUserData checkJwt(String jwt) throws RuntimeException {

    return JwtUserData.fromString(
        Jwts.parser().setSigningKey(R.JWT_SECRET)
            .parseClaimsJws(jwt).getBody().getSubject());
  }

  /**
   * Проверяет токен восстановления. Если токен корректный, возвращает ID пользователя, который владеет указанным токеном восстановления
   * @param refreshToken токен восстановления пользователя
   * @return ID пользователя - владельца токена, если токен корректный
   * @throws NotFoundException если токен восстановления не найден
   * @throws DateTimeException если полученный токен восстановления истек
   */
  @Override
  public String checkRefreshToken(String refreshToken) throws NotFoundException, DateTimeException {

    // Получение модели токена восстановления из бд
    String prepareRefreshToken = DBSecuritySupport.preparePostgreSQLString(refreshToken);
    RefreshTokenEntity refreshTokenEntityFromRequest = refreshTokenRepository.findByToken(prepareRefreshToken)
        .orElseThrow(() -> new NotFoundException(String.format("Refresh token %s not found", refreshToken)));

    // Проверка токена восстановления на свежесть
    DateTimeSupport.checkDateTimeAfterNow(refreshTokenEntityFromRequest.getDateExpiration());

    return refreshTokenEntityFromRequest.getIdUser();
  }

  /**
   * Проверяет код входа. Если код корректный, возвращает ID пользователя, который владеет указанным кодом входа
   * @param loginCode код входа пользователя
   * @return ID пользователя - владельца кода, если код корректный
   * @throws NotFoundException если код входа не найден
   * @throws DateTimeException если полученный код входа истек
   */
  @Override
  public String checkLoginCode(int loginCode) throws NotFoundException, DateTimeException {

    // Получение модели кода входа из бд
    LoginCodeEntity loginCodeEntityFromRequest = loginCodeRepository.findByCode(loginCode).
        orElseThrow(() -> new NotFoundException(String.format("Login code %s not found", loginCode)));

    // Проверка кода входа на свежесть
    DateTimeSupport.checkDateTimeAfterNow(loginCodeEntityFromRequest.getDateExpiration());

    return loginCodeEntityFromRequest.getIdUser();
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
    loginCodeRepository.save(LoginCodeEntity.builder()
                                         .idUser(idUser)
                                         .code(loginCode)
                                         .dateExpiration(DateTimeSupport.getExpiration(R.LOGIN_EXPIRATION_SEC))
                                         .build());
    log.info("DB. Login code \"{}\" created for the user with id \"{}\"", loginCode, idUser);

    return loginCode;
  }

  /**
   * Генерирует новые токены доступа (jwt) и восстановления по ID пользователя.
   * Сгенерированный токен восстановления сохраняется в бд.
   * Существование пользователя по ID не проверяется.
   * @param idUser ID пользователя
   * @return контейнер с токенами доступа (jwt) и восстановления
   */
  @Override
  public AuthData generateAndSaveAuthTokens(String idUser) {

    // Проверка существования пользователя по его ролям
    String prepareIdUser = DBSecuritySupport.preparePostgreSQLString(idUser);
    List<RoleEntity> roleEntitySet = roleRepository.findAllByIdUser(prepareIdUser);

    // Генерация токенов
    JwtData jwtData = generateJwt(idUser, roleEntitySet);
    String newRefreshToken = generateAndSaveRefreshToken(idUser);

    return AuthData
        .builder()
        .jwtData(jwtData)
        .refreshToken(newRefreshToken)
        .build();
  }

  /**
   * Удаляет токен восстановления по ID пользователя
   * @param refreshToken токен восстановления пользователя
   * @throws NotFoundException если токен восстановления не найден
   */
  @Override
  @Transactional
  public void removeRefreshToken(String refreshToken) throws NotFoundException {

    String prepareRefreshToken = DBSecuritySupport.preparePostgreSQLString(refreshToken);

    if (!refreshTokenRepository.existsByToken(prepareRefreshToken)) {
      throw new NotFoundException(String.format("Refresh token \"%s\" not found", refreshToken));
    }

    refreshTokenRepository.deleteByToken(prepareRefreshToken);
    log.info("DB. Refresh token \"{}\" deleted.", refreshToken);
  }

  /**
   * Удаляет код входа по его значения
   * @param loginCode код входа пользователя
   * @throws NotFoundException если код входа не найден
   */
  @Override
  public void removeLoginCode(int loginCode) throws NotFoundException {

    if (!loginCodeRepository.existsByCode(loginCode)) {
      throw new NotFoundException(String.format("Login code \"%s\" not found", loginCode));
    }

    loginCodeRepository.deleteByCode(loginCode);
    log.info("DB. User login code \"{}\" deleted.", loginCode);
  }

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param id ID пользователя
   * @param roleEntityList список ролей пользователя
   * @return сгенерированный jwt и его полезную нагрузку {@link JwtUserData}
   */
  private JwtData generateJwt(String id, List<RoleEntity> roleEntityList) {

    JwtUserData userData = JwtUserData
        .builder()
        .idUser(id)
        .nameRoleList(roleEntityList
                          .stream()
                          .map(RoleEntity::getName)
                          .collect(Collectors.toList()))
        .build();

    return JwtData
        .builder()
        .tokenJwt(Jwts.builder()
                      .setSubject(userData.toString())
                      .setExpiration(Date.from(
                          DateTimeSupport.getExpiration(R.JWT_EXPIRATION_SEC).toInstant(ZoneOffset.UTC)))
                      .signWith(SignatureAlgorithm.HS512, R.JWT_SECRET)
                      .compact())
        .userData(userData)
        .build();
  }

  /**
   * Генерирует и сохраняет токен восстановления указанной длины из символов A-Za-z0-9 для указанного пользователя
   * @param idUser ID пользователя
   * @return сгенерированный токен восстановления
   */
  private String generateAndSaveRefreshToken(String idUser) {

    String refreshToken = RandomValue.randomString(R.REFRESH_LENGTH);

    // Сохранение токена восстановления
    refreshTokenRepository.save(RefreshTokenEntity
                                    .builder()
                                    .idUser(idUser)
                                    .token(refreshToken)
                                    .dateExpiration(DateTimeSupport.getExpiration(R.REFRESH_EXPIRATION_SEC))
                                    .build());
    log.info("DB. Refresh token \"{}\" created for the user with id \"{}\"", refreshToken, idUser);

    return refreshToken;
  }
}
