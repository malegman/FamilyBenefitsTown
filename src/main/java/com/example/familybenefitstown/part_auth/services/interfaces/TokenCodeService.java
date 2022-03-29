package com.example.familybenefitstown.part_auth.services.interfaces;

import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_auth.models.AuthData;
import com.example.familybenefitstown.part_auth.models.JwtUserData;

/**
 * Интерфейс сервиса для работы с токенами доступа (в формате jwt) и восстановления
 */
public interface TokenCodeService {

  /**
   * Генерирует и сохраняет код для входа в систему для указанного пользователя
   * @param idUser ID пользователя
   * @return сгенерированный код
   */
  int generateAndSaveLoginCode(String idUser);

  /**
   * Генерирует новые токены доступа (jwt) и восстановления по ID пользователя.
   * Сгенерированный токен восстановления сохраняется в бд.
   * Существование пользователя по ID не проверяется.
   * @param idUser ID пользователя
   * @return контейнер с токенами доступа (jwt) и восстановления
   */
  AuthData generateAndSaveAuthTokens(String idUser);

  /**
   * Проверяет jwt. Если jwt корректный, возвращает данные пользователя из строки, формата токена jwt
   * @param jwt токен пользователя, jwt
   * @return данные пользователя, если jwt корректный
   * @throws RuntimeException если не удалось извлечь данные пользователя из строки
   */
  JwtUserData checkJwt(String jwt) throws RuntimeException;

  /**
   * Проверяет токен восстановления. Если токен корректный, возвращает ID пользователя, который владеет указанным токеном восстановления
   * @param refreshToken токен восстановления пользователя
   * @return ID пользователя - владельца токена, если токен корректный
   * @throws NotFoundException если токен восстановления не найден
   * @throws DateTimeException если полученный токен восстановления истек
   */
  String checkRefreshToken(String refreshToken) throws NotFoundException, DateTimeException;

  /**
   * Проверяет код входа. Если код корректный, возвращает ID пользователя, который владеет указанным кодом входа
   * @param loginCode код входа пользователя
   * @return ID пользователя - владельца кода, если код корректный
   * @throws NotFoundException если код входа не найден
   * @throws DateTimeException если полученный код входа истек
   */
  String checkLoginCode(int loginCode) throws NotFoundException, DateTimeException;

  /**
   * Удаляет токен восстановления по ID пользователя
   * @param refreshToken токен восстановления пользователя
   * @throws NotFoundException если токен восстановления не найден
   */
  void removeRefreshToken(String refreshToken) throws NotFoundException;

  /**
   * Удаляет код входа по его значения
   * @param loginCode код входа пользователя
   * @throws NotFoundException если код входа не найден
   */
  void removeLoginCode(int loginCode) throws NotFoundException;
}

