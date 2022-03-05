package com.example.familybenefitstown.security.service.inface;

import com.example.familybenefitstown.dto.entity.RoleEntity;
import com.example.familybenefitstown.security.web.auth.JwtUserData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Сервис для работы с токеном доступа (в формате jwt) и кодом для входа
 */
public interface TokenCodeService {

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param id ID пользователя
   * @param roleEntitySet множество ролей пользователя
   * @param request http запрос на вход систему
   * @return сгенерированный jwt
   */
  String generateJwt(String id, Set<RoleEntity> roleEntitySet, HttpServletRequest request);

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
  JwtUserData authFromStringJwt(String jwt) throws RuntimeException;

  /**
   * Проверяет наличие в бд токена jwt по ID данного пользователя.
   * Таким образом проверяется, был ли осуществлен выход пользователя из системы.
   * @param idUser ID пользователя
   * @return true, если токен jwt данного пользователя есть в бд
   */
  boolean existsJwtById(String idUser);

  /**
   * Создает код для входа в систему.
   * Код представляет собой 6-ти значное число
   * @return сгенерированный код
   */
  int generateLoginCode();
}

