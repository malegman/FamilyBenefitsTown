package com.example.familybenefitstown.part_res_rest_api.controllers;

import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminSave;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.SuperAdminService;
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
   * Обрабатывает POST запрос "/api/sa/admins" на создание администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param adminSave объект запроса для сохранения администратора
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/api/sa/admins",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> create(@RequestBody AdminSave adminSave,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/api/sa/admins\": Request in controller", requestAddress);

    // Если тело запроса пустое
    if (adminSave == null) {
      log.warn("{} POST \"/api/sa/admins\": Request body \"adminSave\" is empty", requestAddress);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      superAdminService.create(adminSave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (AlreadyExistsException | InvalidEmailException | InvalidStringException e) {
      // Администратор или пользователь с указанным email существует.
      // Строка в поле "email" не является email.
      // Некорректное строковое поле объекта запроса.
      log.warn("{} POST \"/api/sa/admins\": {}", requestAddress, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает DELETE запрос "/api/sa/admins/{id}" на удаление администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN".
   * Супер-администратор не может удалить у себя роль администратора. Для удаления, необходимо передать роль супер-администратора
   * @param idAdmin ID администратора
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @DeleteMapping(
      value = "/api/sa/admins/{id}")
  public ResponseEntity<?> delete(@PathVariable(name = "id") String idAdmin,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} DELETE \"/api/sa/admins/{}\": Request in controller", requestAddress, idAdmin);

    try {
      superAdminService.delete(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.warn("{} DELETE \"/api/sa/admins/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/api/sa/from-user/{id}" на добавление роли "ROLE_ADMIN" пользователю.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idUser ID пользователя, которому добавляется роль
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/api/sa/from-user/{id}")
  public ResponseEntity<?> fromUser(@PathVariable(name = "id") String idUser,
                                    HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/api/sa/from-user/{}\": Request in controller", requestAddress, idUser);

    try {
      superAdminService.fromUser(idUser);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден пользователь с ролью "ROLE_USER"
      log.warn("{} POST \"/api/sa/from-user/{}\": {}", requestAddress, idUser, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (AlreadyExistsException e) {
      // Пользователь имеет роль "ROLE_ADMIN"
      log.warn("{} POST \"/api/sa/from-user/{}\": {}", requestAddress, idUser, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/api/sa/to-user/{id}" на добавление роли "ROLE_USER" администратору.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idAdmin ID администратора, которому добавляется роль
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/api/sa/to-user/{id}")
  public ResponseEntity<?> toUser(@PathVariable(name = "id") String idAdmin,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/api/sa/to-user/{}\": Request in controller", requestAddress, idAdmin);

    try {
      superAdminService.toUser(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден пользователь с ролью "ROLE_ADMIN"
      log.warn("{} POST \"/api/sa/to-user/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (AlreadyExistsException e) {
      // Пользователь имеет роль "ROLE_USER"
      log.warn("{} POST \"/api/sa/to-user/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/api/sa/to-super/{id}" на передачу роли "ROLE_SUPER_ADMIN" другому администратору.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idAdmin ID администратора, которому передается роль
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/api/sa/to-super/{id}")
  public ResponseEntity<?> toSuper(@PathVariable(name = "id") String idAdmin,
                                   HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/api/sa/to-user/{}\": Request in controller", requestAddress, idAdmin);

    try {
      superAdminService.toSuper(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.warn("{} POST \"/api/sa/to-super/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
