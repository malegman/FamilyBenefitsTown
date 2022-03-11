package com.example.familybenefitstown.security.services.interfaces;

import com.example.familybenefitstown.dto.entities.strong.RoleEntity;
import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.security.web.auth.AuthTokens;
import com.example.familybenefitstown.security.web.auth.JwtUserData;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Интерфейс сервиса для работы с токенами доступа (в формате jwt) и восстановления и кодом для входа
 */
public interface TokenCodeService {

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param id ID пользователя
   * @param roleEntityList список ролей пользователя
   * @return сгенерированный jwt
   */
  String generateJwt(String id, List<RoleEntity> roleEntityList);

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param userData данные доступа из токена доступа jwt
   * @return сгенерированный jwt
   */
  String generateJwt(JwtUserData userData);

  /**
   * Извлекает данные пользователя из строки, формата токена jwt
   * @param jwt токен пользователя, jwt
   * @return данные авторизации
   * @throws RuntimeException если не удалось извлечь данные пользователя из строки
   */
  JwtUserData dataFromJwt(String jwt) throws RuntimeException;

  /**
   * Генерирует и сохраняет токен восстановления указанной длины из символов A-Za-z0-9 для указанного пользователя
   * @param idUser ID пользователя
   * @return сгенерированный токен восстановления
   */
  String generateAndSaveRefreshToken(String idUser);

  /**
   * Возвращает ID пользователя, который владеет указанным токеном восстановления
   * @param refreshToken токена восстановления пользователя
   * @return {@code true}, если токен восстановления пользователя есть в бд
   * @throws NotFoundException если токен восстановления не найден
   * @throws DateTimeException если полученный токен восстановления истек
   */
  String getIdOfNotExpiredRefreshToken(String refreshToken) throws NotFoundException, DateTimeException;

  /**
   * Генерирует и сохраняет код для входа в систему для указанного пользователя
   * @param idUser ID пользователя
   * @return сгенерированный код
   */
  int generateAndSaveLoginCode(String idUser);

  /**
   * Извлекает токен доступа jwt из запроса клиента. {@code null}, если токен не найден
   * @param request запрос клиента
   * @return токен доступа пользователя.
   */
  String jwtFromRequest(HttpServletRequest request);

  /**
   * Извлекает токен восстановления из запроса клиента. {@code null}, если токен не найден
   * @param request запрос клиента
   * @return токен восстановления пользователя
   */
  String refreshTokenFromRequest(HttpServletRequest request);

  /**
   * Генерирует новые токены доступа (jwt) и восстановления по коду входа пользователя, полученного по почте
   * @param emailLoginCode код входа пользователя
   * @return контейнер с токенами доступа (jwt) и восстановления
   */
  AuthTokens generateAuthTokens(int emailLoginCode) throws NotFoundException, DateTimeException;

  /**
   * Генерирует новые токены доступа (jwt) и восстановления по ID пользователя.
   * Сгенерированный токен восстановления сохраняется в бд.
   * @param idUser ID пользователя
   * @return контейнер с токенами доступа (jwt) и восстановления
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  AuthTokens generateAuthTokens(String idUser) throws NotFoundException;

  /**
   * Удаляет токен восстановления по ID пользователя
   * @param refreshToken токен восстановления пользователя
   */
  void removeRefreshToken(String refreshToken);

  /**
   * Удаляет код входа по ID пользователя
   * @param idUser ID пользователя
   */
  void removeLoginCodeByIdUser(String idUser);

  /**
   * Удаляет токен восстановления и код входа по ID пользователя
   * @param idUser ID пользователя
   */
  void removeTokenCodeByIdUser(String idUser);
}

