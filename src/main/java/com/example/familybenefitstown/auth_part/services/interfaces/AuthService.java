package com.example.familybenefitstown.auth_part.services.interfaces;

import com.example.familybenefitstown.res_part_rest_api.api_models.auth.LoginResponse;
import com.example.familybenefitstown.exceptions.NotFoundException;
import org.springframework.mail.MailException;

import javax.servlet.http.HttpServletRequest;

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
   */
  LoginResponse login(String email, int loginCode) throws NotFoundException;

  /**
   * Выход из системы
   * @param request http запрос, содержащий токен восстановления
   */
  void logout(HttpServletRequest request);
}

