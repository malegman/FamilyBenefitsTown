package com.example.familybenefitstown.part_auth.services.interfaces;

import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_auth.models.JwtUserData;
import com.example.familybenefitstown.part_auth.models.LoginResponse;
import org.springframework.mail.MailException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
 */
public interface AuthService {

  /**
   * Отправляет на почту пользователю код для входа в систему
   * @param email почта пользователя
   * @throws NotFoundException если пользователь с данным email не найден
   * @throws MailException если не удалось отправить сообщение
   */
  void preLogin(String email) throws NotFoundException, MailException;

  /**
   * Вход в систему по почте и коду для входа
   * @param email почта пользователя
   * @param loginCode код для входа пользователя
   * @return объект ответа на вход в систему
   * @throws NotFoundException если не найден пользователь по указанным данным
   * @throws DateTimeException если полученный код входа истек
   */
  LoginResponse login(String email, int loginCode) throws NotFoundException, DateTimeException;

  /**
   * Выход из системы. Удаляет токен восстановления авторизованного пользователя
   * @param idUser ID существующего пользователя, запрашивающего выход
   */
  void logout(String idUser);

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
  Optional<JwtUserData> authenticate(HttpServletRequest request, HttpServletResponse response);
}

