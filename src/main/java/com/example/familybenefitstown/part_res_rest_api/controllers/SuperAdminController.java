package com.example.familybenefitstown.part_res_rest_api.controllers;

import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminSave;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер запросов, связанных с супер-администратором
 */
@RestController
public class SuperAdminController {

  /**
   * Интерфейс сервиса, управляющего объектом "администратор"
   */
  private final SuperAdminService superAdminService;

  /**
   * Конструктор для инициализации интерфейса сервиса
   * @param superAdminService интерфейс сервиса, управляющего объектом "супер-администратор"
   */
  @Autowired
  public SuperAdminController(SuperAdminService superAdminService) {
    this.superAdminService = superAdminService;
  }

  /**
   * Обрабатывает POST запрос "/api/sa/admins" на создание администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param adminSave объект запроса для сохранения администратора
   * @return код ответа, результат обработки запроса
   * @throws AlreadyExistsException если администратор или пользователь с указанным email уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   * @throws InvalidEmailException если указанный "email" не является email
   */
  @PostMapping(
      value = "/api/sa/admins",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> create(@RequestBody AdminSave adminSave)
      throws AlreadyExistsException, InvalidStringException, InvalidEmailException {

    superAdminService.create(adminSave);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Обрабатывает DELETE запрос "/api/sa/admins/{id}" на удаление администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN".
   * Супер-администратор не может удалить у себя роль администратора. Для удаления, необходимо передать роль супер-администратора
   * @param idAdmin ID администратора
   * @return код ответа, результат обработки запроса
   * @throws NotFoundException если администратор с указанным ID не найден
   */
  @DeleteMapping(
      value = "/api/sa/admins/{id}")
  public ResponseEntity<?> delete(@PathVariable(name = "id") String idAdmin) throws NotFoundException {

    superAdminService.delete(idAdmin);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Обрабатывает POST запрос "/api/sa/from-user/{id}" на добавление роли "ROLE_ADMIN" пользователю.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idUser ID пользователя, которому добавляется роль
   * @return код ответа, результат обработки запроса
   * @throws AlreadyExistsException если пользователь имеет роль "ROLE_ADMIN"
   * @throws NotFoundException если пользователь с данным ID и ролью "ROLE_USER" не найден
   */
  @PostMapping(
      value = "/api/sa/from-user/{id}")
  public ResponseEntity<?> fromUser(@PathVariable(name = "id") String idUser)
      throws AlreadyExistsException, NotFoundException {

    superAdminService.fromUser(idUser);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Обрабатывает POST запрос "/api/sa/to-user/{id}" на добавление роли "ROLE_USER" администратору.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idAdmin ID администратора, которому добавляется роль
   * @return код ответа, результат обработки запроса
   * @throws AlreadyExistsException если пользователь имеет роль "ROLE_USER"
   * @throws NotFoundException если пользователь с данным ID и ролью "ROLE_ADMIN" не найден
   */
  @PostMapping(
      value = "/api/sa/to-user/{id}")
  public ResponseEntity<?> toUser(@PathVariable(name = "id") String idAdmin)
      throws AlreadyExistsException, NotFoundException {

    superAdminService.toUser(idAdmin);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Обрабатывает POST запрос "/api/sa/to-super/{id}" на передачу роли "ROLE_SUPER_ADMIN" другому администратору.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idAdmin ID администратора, которому передается роль
   * @return код ответа, результат обработки запроса
   * @throws NotFoundException если администратор с данным ID не найден
   */
  @PostMapping(
      value = "/api/sa/to-super/{id}")
  public ResponseEntity<?> toSuper(@PathVariable(name = "id") String idAdmin) throws NotFoundException {

    superAdminService.toSuper(idAdmin);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
