package com.example.familybenefitstown.controllers;

import com.example.familybenefitstown.api_models.auth.LoginResponse;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.services.interfaces.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Контроллер запросов, связанных с аутентификацией и авторизацией в системе
 */
@Slf4j
@RestController
public class AuthController {

  /**
   * Интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  private final AuthService authService;

  /**
   * Конструктор для инициализации сервисов
   * @param authService интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Обрабатывает POST запрос "/auth/pre-login" на получение кода для входа в систему.
   * Для анонимного клиента.
   * @param email почта пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/auth/pre-login")
  public ResponseEntity<?> preLogin(@RequestParam(name = "e") String email,
                                    HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/auth/pre-login?e={}\": Request in controller", requestAddress, email);

    try {
      authService.preLogin(email);
      return ResponseEntity.status(HttpStatus.OK).build();

    } catch (NotFoundException e) {
      // Не найден пользователь
      log.warn("{} POST \"/auth/pre-login?e={}\": {}", requestAddress, email, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/auth/login" на вход в систему.
   * Для анонимного клиента.
   * @param email почта пользователя
   * @param loginCode код для входа пользователя
   * @param request http запрос
   * @return объект ответа на вход, если запрос выполнен успешно, и код ответа
   */
  @PostMapping(
      value = "/auth/login",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<LoginResponse> login(@RequestParam(name = "e") String email,
                                             @RequestParam(name = "lc") int loginCode,
                                             HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/auth/login?e={}&lc={}\": Request in controller", requestAddress, email, loginCode);

    LoginResponse loginResponse = authService.login(email, loginCode);
    return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
  }

  /**
   * Обрабатывает POST запрос "/auth/logout" на выход из системы.
   * Для выполнения запроса клиент должен быть аутентифицирован.
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/auth/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/auth/logout\": Request in controller", requestAddress);

    authService.logout(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}

