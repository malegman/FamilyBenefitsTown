package com.example.familybenefitstown.controller;

import com.example.familybenefitstown.api_model.system.LoginRequest;
import com.example.familybenefitstown.api_model.system.LoginResponse;
import com.example.familybenefitstown.api_model.system.PreLoginRequest;
import com.example.familybenefitstown.exception.NotFoundException;
import com.example.familybenefitstown.resource.R;
import com.example.familybenefitstown.security.web.auth.JwtUserData;
import com.example.familybenefitstown.service.inface.SystemService;
import com.example.familybenefitstown.service.model.ServiceLoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Контроллер запросов, связанных с системой
 */
@Slf4j
@RestController
public class SystemController {

  /**
   * Интерфейс сервиса, отвечающего за системные функции
   */
  private final SystemService systemService;

  /**
   * Конструктор для инициализации интерфейса сервиса
   * @param systemService интерфейс сервиса, отвечающего за системные функции
   */
  @Autowired
  public SystemController(SystemService systemService) {
    this.systemService = systemService;
  }

  /**
   * Обрабатывает POST запрос "/pre-login" на получение кода для входа в систему.
   * Для анонимного клиента.
   * @param preLoginRequest Объект запроса пользователя для получения кода для входа в систему
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(value = "/pre-login",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> preLogin(@RequestBody PreLoginRequest preLoginRequest,
                                    HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    if (preLoginRequest == null) {
      log.warn("{} POST \"/pre-login\": Request body \"preLoginRequest\" is empty", userIp);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      systemService.preLogin(preLoginRequest);
      return ResponseEntity.status(HttpStatus.OK).build();

    } catch (NotFoundException e) {
      // Не найден пользователь
      log.error("{} POST \"/pre-login\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/login" на вход в систему.
   * Для анонимного клиента.
   * Помещает токен доступа в заголовок ответа.
   * @param loginRequest объект запроса пользователя для входа в систему
   * @param request http запрос
   * @return объект ответа на вход, если запрос выполнен успешно, и код ответа
   */
  @PostMapping(
      value = "/login",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                             HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    if (loginRequest == null) {
      log.warn("{} POST \"/login\": Request body \"loginRequest\" is empty", userIp);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    ServiceLoginResponse serviceLoginResponse;

    try {
      serviceLoginResponse = systemService.login(loginRequest, request);

    } catch (NotFoundException e) {
      // Не найден пользователь
      log.error("{} POST \"/login\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Получение тела ответа, токена восстановления и токена доступа jwt
    LoginResponse loginResponse = serviceLoginResponse.getLoginResponse();
    String jwt = serviceLoginResponse.getJwt();

    // Установление jwt в заголовок "Authorization" и токена восстановления в заголовок "Set-Cookie"
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add(R.AUTHORIZATION_HEADER,
                        String.format(R.AUTHORIZATION_VALUE_PATTERN, jwt));

    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(loginResponse);
  }

  /**
   * Обрабатывает POST запрос "/logout" на выход из системы.
   * Для выполнения запроса клиент должен быть аутентифицирован.
   * @param userData данные пользователя из jwt, отправившего запрос, для получения ID пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/logout")
  public ResponseEntity<?> logout(@AuthenticationPrincipal JwtUserData userData,
                                  HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    try {
      systemService.logout(userData);
      return ResponseEntity.status(HttpStatus.OK).build();

    } catch (NotFoundException e) {
      // Не найден пользователь
      log.error("{} POST \"/logout\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/refresh" на обновление токена доступа.
   * Для выполнения запроса клиент должен быть аутентифицирован.
   * Помещает токен доступа в заголовок ответа.
   * @param userData данные пользователя из jwt, отправившего запрос, для получения ID пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(value = "/refresh")
  public ResponseEntity<?> refresh(@AuthenticationPrincipal JwtUserData userData,
                                   HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    String jwt;

    try {
      jwt = systemService.refresh(userData);

    } catch (NotFoundException e) {
      // Не найден пользователь
      log.error("{} POST \"/logout\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Установление jwt в заголовок "Authorization" и токена восстановления в заголовок "Set-Cookie"
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add(R.AUTHORIZATION_HEADER,
                        String.format(R.AUTHORIZATION_VALUE_PATTERN, jwt));

    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).build();
  }
}

