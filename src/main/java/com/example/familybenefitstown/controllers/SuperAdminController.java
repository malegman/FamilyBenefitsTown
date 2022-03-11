package com.example.familybenefitstown.controllers;

import com.example.familybenefitstown.api_models.admin.AdminSave;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.services.interfaces.SuperAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Контроллер запросов, связанных с супер-администратором
 */
@Slf4j
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
   * Обрабатывает POST запрос "/sa/admins" на создание администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param adminSave объект запроса для сохранения администратора
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/sa/admins",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> create(@RequestBody AdminSave adminSave,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/sa/admins\": Request in controller", requestAddress);

    // Если тело запроса пустое
    if (adminSave == null) {
      log.warn("{} POST \"/sa/admins\": Request body \"adminSave\" is empty", requestAddress);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      superAdminService.create(adminSave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (AlreadyExistsException | InvalidEmailException e) {
      // Администратор или пользователь с указанным email существует.
      // Строка в поле "email" не является email.
      log.warn("{} POST \"/sa/admins\": {}", requestAddress, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает DELETE запрос "/sa/admins/{id}" на удаление администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN".
   * Супер-администратор не может удалить у себя роль администратора. Для удаления, необходимо передать роль супер-администратора
   * @param idAdmin ID администратора
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @DeleteMapping(
      value = "/sa/admins/{id}")
  public ResponseEntity<?> delete(@PathVariable(name = "id") String idAdmin,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} DELETE \"/sa/admins/{}\": Request in controller", requestAddress, idAdmin);

    try {
      superAdminService.delete(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.warn("{} DELETE \"/sa/admins/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/sa/from-user/{id}" на добавление роли "ROLE_ADMIN" пользователю.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idUser ID пользователя, которому добавляется роль
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/sa/from-user/{id}")
  public ResponseEntity<?> fromUser(@PathVariable(name = "id") String idUser,
                                    HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/sa/from-user/{}\": Request in controller", requestAddress, idUser);

    try {
      superAdminService.fromUser(idUser);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден пользователь с ролью "ROLE_USER"
      log.warn("{} POST \"/sa/from-user/{}\": {}", requestAddress, idUser, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (AlreadyExistsException e) {
      // Пользователь имеет роль "ROLE_ADMIN"
      log.warn("{} POST \"/sa/from-user/{}\": {}", requestAddress, idUser, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/sa/to-user/{id}" на добавление роли "ROLE_USER" администратору.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idAdmin ID администратора, которому добавляется роль
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/sa/to-user/{id}")
  public ResponseEntity<?> toUser(@PathVariable(name = "id") String idAdmin,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/sa/to-user/{}\": Request in controller", requestAddress, idAdmin);

    try {
      superAdminService.toUser(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден пользователь с ролью "ROLE_ADMIN"
      log.warn("{} POST \"/sa/to-user/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (AlreadyExistsException e) {
      // Пользователь имеет роль "ROLE_USER"
      log.warn("{} POST \"/sa/to-user/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/sa/to-super/{id}" на передачу роли "ROLE_SUPER_ADMIN" другому администратору.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idAdmin ID администратора, которому передается роль
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/sa/to-super/{id}")
  public ResponseEntity<?> toSuper(@PathVariable(name = "id") String idAdmin,
                                   HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/sa/to-user/{}\": Request in controller", requestAddress, idAdmin);

    try {
      superAdminService.toSuper(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.warn("{} POST \"/sa/to-super/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
