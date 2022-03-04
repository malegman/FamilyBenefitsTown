package com.example.familybenefitstown.service.inface;

import com.example.familybenefitstown.api_model.system.LoginRequest;
import com.example.familybenefitstown.api_model.system.PreLoginRequest;
import com.example.familybenefitstown.exception.NotFoundException;
import com.example.familybenefitstown.security.web.auth.JwtAuthenticationUserData;
import com.example.familybenefitstown.service.model.ServiceLoginResponse;
import org.springframework.mail.MailException;

import javax.servlet.http.HttpServletRequest;

/**
 * Интерфейс сервиса, отвечающего за системные функции
 */
public interface SystemService {

  /**
   * Отправляет на почту пользователю код для входа в систему
   * @param preLoginRequest объект запроса пользователя для получения кода для входа в систему
   * @throws NotFoundException если не найден пользователь по указанным данным
   * @throws MailException если не удалось отправить сообщение
   */
  void preLogin(PreLoginRequest preLoginRequest) throws NotFoundException, MailException;

  /**
   * Вход в систему
   * @param loginRequest объект запроса пользователя для входа в систему
   * @param request http запрос
   * @return объект ответа на вход в систему
   * @throws NotFoundException если не найден пользователь по указанным данным
   */
  ServiceLoginResponse login(LoginRequest loginRequest, HttpServletRequest request) throws NotFoundException;

  /**
   * Выход из системы
   * @param userAuth данные доступа из токена доступа jwt
   * @throws NotFoundException если не найден пользователь по указанным данным
   */
  void logout(JwtAuthenticationUserData userAuth) throws NotFoundException;

  /**
   * Обновляет токен доступа пользователя
   * @param userAuth данные доступа из токена доступа jwt
   * @return новый токен доступа jwt
   * @throws NotFoundException если не найден пользователь по указанным данным
   */
  String refresh(JwtAuthenticationUserData userAuth) throws NotFoundException;
}

