package com.example.familybenefitstown.part_res_rest_api.controllers;

import com.example.familybenefitstown.exceptions.*;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserInitData;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserSave;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Контроллер запросов, связанных с пользователем
 */
@Slf4j
@RestController
public class UserController {

  /**
   * Интерфейс сервиса, управляющего объектом "пользователь"
   */
  private final UserService userService;

  /**
   * Конструктор для инициализации интерфейса сервиса
   * @param userService интерфейс сервиса, управляющего объектом "пользователь"
   */
  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Обрабатывает POST запрос "/users" на создание пользователя. Регистрация гостя
   * Для незарегистрированного клиента.
   * @param userSave объект запроса для сохранения пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/users",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> create(@RequestBody UserSave userSave,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/users\": Request in controller", requestAddress);

    // Если тело запроса пустое
    if (userSave == null) {
      log.warn("{} POST \"/users\": Request body \"userSave\" is empty", requestAddress);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      userService.create(userSave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найдены критерии или город
      log.warn("{} POST \"/users\": {}", requestAddress, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (AlreadyExistsException | InvalidEmailException | DateTimeException | DateFormatException | InvalidStringException e) {
      // Администратор или пользователь с указанным email существует.
      // Строка в поле "email" не является email.
      // Даты позже текущей даты.
      // Даты не соответствуют формату "dd.mm.yyyy".
      // Некорректное строковое поле объекта запроса.
      log.warn("{} POST \"/users\": {}", requestAddress, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает GET запрос "/users/{id}" на получение информации о пользователе.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_USER"
   * @param idUser ID пользователя
   * @param request http запрос
   * @return информация о пользователе, если запрос выполнен успешно, и код ответа
   */
  @GetMapping(
      value = "/users/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<UserInfo> read(@PathVariable(name = "id") String idUser,
                                       HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} GET \"/users/{}\": Request in controller", requestAddress, idUser);

    try {
      UserInfo userInfo = userService.read(idUser);
      return ResponseEntity.status(HttpStatus.OK).body(userInfo);

    } catch (NotFoundException e) {
      // Не найден пользователь
      log.error("{} GET \"/users/{}\": {}", requestAddress, idUser, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает PUT запрос "/users/{id}" на обновление пользователя.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_USER"
   * @param idUser ID пользователя
   * @param userSave объект запроса для сохранения пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PutMapping(
      value = "/users/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> update(@PathVariable(name = "id") String idUser,
                                  @RequestBody UserSave userSave,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} PUT \"/users/{}\": Request in controller", requestAddress, idUser);

    // Если тело запроса пустое
    if (userSave == null) {
      log.warn("{} PUT \"/users/{}\": Request body \"userSave\" is empty", requestAddress, idUser);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      userService.update(idUser, userSave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден пользователь или не найдены критерии или город
      log.warn("{} PUT \"/users/{}\": {}", requestAddress, idUser, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (InvalidEmailException | DateTimeException | DateFormatException | AlreadyExistsException | InvalidStringException e) {
      // Строка в поле "email" не является email.
      // Даты позже текущей даты.
      // Даты не соответствуют формату "dd.mm.yyyy".
      // Пользователь с отличным ID и данным email уже существует.
      // Некорректное строковое поле объекта запроса.
      log.warn("{} PUT \"/users/{}\": {}", requestAddress, idUser, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает DELETE запрос "/users/{id}" на удаление пользователя.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_USER"
   * @param idUser ID пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @DeleteMapping(
      value = "/users/{id}")
  public ResponseEntity<?> delete(@PathVariable(name = "id") String idUser,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} DELETE \"/users/{}\": Request in controller", requestAddress, idUser);

    try {
      userService.delete(idUser);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден пользователь
      log.warn("{} DELETE \"/users/{}\": {}", requestAddress, idUser, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает GET запрос "/users/init-data" на получение дополнительных данных для пользователя.
   * Данные содержат в себе множества кратких информаций о городах и полных критериях.
   * Выполнить запрос может любой клиент
   * @param request http запрос
   * @return дополнительные данные для пользователя и код ответа
   */
  @GetMapping(
      value = "/users/init-data",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<UserInitData> getInitData(HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} GET \"/users/init-data\": Request in controller", requestAddress);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userService.getInitData());
  }
}

