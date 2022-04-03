package com.example.familybenefitstown.part_res_rest_api.controllers;

import com.example.familybenefitstown.exceptions.*;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserInitData;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserSave;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер запросов, связанных с пользователем
 */
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
   * Обрабатывает POST запрос "/api/users" на создание пользователя. Регистрация гостя
   * Для незарегистрированного клиента.
   * @param userSave объект запроса для сохранения пользователя
   * @return код ответа, результат обработки запроса
   * @throws DateTimeException если даты рождения пользователя или детей позже текущей даты
   * @throws AlreadyExistsException если пользователь с отличным ID и данным email уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   * @throws NotFoundException если пользователь, город или критерии с указанными данными не найдены
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws DateFormatException если даты рождения пользователя или детей не соответствуют формату "dd.mm.yyyy"
   */
  @PostMapping(
      value = "/api/users",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> create(@RequestBody UserSave userSave)
      throws DateTimeException, AlreadyExistsException, InvalidStringException, NotFoundException, InvalidEmailException, DateFormatException {

    userService.create(userSave);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Обрабатывает GET запрос "/api/users/{id}" на получение информации о пользователе.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_USER"
   * @param idUser ID пользователя
   * @return информация о пользователе, если запрос выполнен успешно, и код ответа
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  @GetMapping(
      value = "/api/users/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<UserInfo> read(@PathVariable(name = "id") String idUser) throws NotFoundException {

    UserInfo userInfo = userService.read(idUser);
    return ResponseEntity.status(HttpStatus.OK).body(userInfo);
  }

  /**
   * Обрабатывает PUT запрос "/api/users/{id}" на обновление пользователя.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_USER"
   * @param idUser ID пользователя
   * @param userSave объект запроса для сохранения пользователя
   * @return код ответа, результат обработки запроса
   * @throws DateTimeException если даты рождения пользователя или детей позже текущей даты
   * @throws AlreadyExistsException если пользователь с отличным ID и данным email уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   * @throws NotFoundException если пользователь, город или критерии с указанными данными не найдены
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws DateFormatException если даты рождения пользователя или детей не соответствуют формату "dd.mm.yyyy"
   */
  @PutMapping(
      value = "/api/users/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> update(@PathVariable(name = "id") String idUser, @RequestBody UserSave userSave)
      throws DateTimeException, AlreadyExistsException, InvalidStringException, NotFoundException, InvalidEmailException, DateFormatException {

    userService.update(idUser, userSave);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Обрабатывает DELETE запрос "/api/users/{id}" на удаление пользователя.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_USER"
   * @param idUser ID пользователя
   * @return код ответа, результат обработки запроса
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  @DeleteMapping(
      value = "/api/users/{id}")
  public ResponseEntity<?> delete(@PathVariable(name = "id") String idUser) throws NotFoundException {

    userService.delete(idUser);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Обрабатывает GET запрос "/api/users/init-data" на получение дополнительных данных для пользователя.
   * Данные содержат в себе множества кратких информаций о городах и полных критериях.
   * Выполнить запрос может любой клиент
   * @return дополнительные данные для пользователя и код ответа
   */
  @GetMapping(
      value = "/api/users/init-data",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<UserInitData> getInitData() {

    return ResponseEntity.status(HttpStatus.OK).body(userService.getInitData());
  }
}

