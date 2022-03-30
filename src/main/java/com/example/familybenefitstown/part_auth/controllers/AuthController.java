package com.example.familybenefitstown.part_auth.controllers;

import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_auth.models.LoginResponse;
import com.example.familybenefitstown.part_auth.services.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер запросов, связанных с системой, входом и выходом
 */
@RestController
public class AuthController {

  /**
   * Интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  private final AuthService authService;

  /**
   * Конструктор для инициализации интерфейса сервиса
   * @param authService интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Обрабатывает POST запрос "/api/auth/pre-login" на получение кода для входа.
   * Выполнить запрос может только неавторизованный клиент.
   * @param email email пользователя
   * @return код ответа, результат обработки запроса
   * @throws NotFoundException если пользователь с данным email не найден
   */
  @PostMapping(
      value = "/api/auth/pre-login")
  public ResponseEntity<?> preLogin(@RequestParam(name = "e") String email) throws NotFoundException {

    authService.preLogin(email);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Обрабатывает POST запрос "/api/auth/login" на вход пользователя в систему.
   * Выполнить запрос может только неавторизованный клиент.
   * @param email email пользователя
   * @param loginCode код входа пользователя
   * @return информация о пользователе, если запрос выполнен успешно, и код ответа
   * @throws DateTimeException если полученный код входа истек
   * @throws NotFoundException если не найден пользователь по указанным данным
   */
  @PostMapping(
      value = "/api/auth/login",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<LoginResponse> login(@RequestParam(name = "e") String email, @RequestParam(name = "lc") int loginCode)
      throws DateTimeException, NotFoundException {

    LoginResponse loginResponse = authService.login(email, loginCode);
    return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
  }

  /**
   * Обрабатывает POST запрос "/api/auth/logout/{id}" на выход пользователя из системы.
   * Выполнить запрос может только авторизованный клиент.
   * Выход из системы клиент может запросить только своего профиля.
   * @param idUser ID пользователя
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/api/auth/logout/{id}")
  public ResponseEntity<?> logout(@PathVariable(name = "id") String idUser) {

    authService.logout(idUser);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
