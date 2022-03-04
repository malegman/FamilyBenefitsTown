package com.example.familybenefitstown.controller;

import com.example.familybenefitstown.api_model.admin.AdminInfo;
import com.example.familybenefitstown.api_model.admin.AdminSave;
import com.example.familybenefitstown.exception.AlreadyExistsException;
import com.example.familybenefitstown.exception.InvalidEmailException;
import com.example.familybenefitstown.exception.NotFoundException;
import com.example.familybenefitstown.exception.UserRoleException;
import com.example.familybenefitstown.security.web.auth.JwtAuthenticationUserData;
import com.example.familybenefitstown.service.inface.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Контроллер запросов, связанных с администратором
 */
@Slf4j
@RestController
public class AdminController {

  /**
   * Интерфейс сервиса, управляющего объектом "администратор"
   */
  private final AdminService adminService;

  /**
   * Конструктор для инициализации интерфейса сервиса
   * @param adminService интерфейс сервиса, управляющего объектом "администратор"
   */
  @Autowired
  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  /**
   * Обрабатывает POST запрос "/admins" на создание администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param adminSave объект запроса для сохранения администратора
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/admins",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> create(@RequestBody AdminSave adminSave,
                                  HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    // Если тело запроса пустое
    if (adminSave == null) {
      log.warn("{} POST \"/admins\": Request body \"adminSave\" is empty", userIp);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      adminService.create(adminSave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (AlreadyExistsException | InvalidEmailException e) {
      // Администратор или пользователь с указанным email существует.
      // Строка в поле "email" не является email.
      log.error("{} POST \"/admins\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает GET запрос "/admins/{id}" на получение информации об администраторе.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_ADMIN".
   * Администратор может получить информацию только о своем профиле.
   * @param idAdmin ID администратора
   * @param userAuth данные пользователя из jwt, отправившего запрос, для получения ID пользователя
   * @param request http запрос
   * @return информация об администраторе, если запрос выполнен успешно, и код ответа
   */
  @GetMapping(
      value = "/admins/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<AdminInfo> read(@PathVariable(name = "id") String idAdmin,
                                        @AuthenticationPrincipal JwtAuthenticationUserData userAuth,
                                        HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    // Если пользователь пытается получить информацию не о своем профиле
    if (!userAuth.getIdUser().equals(idAdmin)) {
      log.warn("{} GET \"/admins/{id}\": User with id {} tried to read user with id {}", userIp, userAuth.getIdUser(), idAdmin);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      AdminInfo adminInfo = adminService.read(idAdmin);
      return ResponseEntity.status(HttpStatus.OK).body(adminInfo);

    } catch (NotFoundException e) {
      // Не найден администратор
      log.error("{} GET \"/admins/{id}\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает PUT запрос "/admins/{id}" на обновление администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_ADMIN".
   * Администратор может обновить только свой профиль.
   * @param idAdmin ID администратора
   * @param adminSave объект запроса для сохранения администратора
   * @param userAuth данные пользователя из jwt, отправившего запрос, для получения ID пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PutMapping(
      value = "/admins/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> update(@PathVariable(name = "id") String idAdmin,
                                  @RequestBody AdminSave adminSave,
                                  @AuthenticationPrincipal JwtAuthenticationUserData userAuth,
                                  HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    // Если пользователь пытается обновить не свой профиль
    if (!userAuth.getIdUser().equals(idAdmin)) {
      log.warn("{} PUT \"/admins/{id}\": User with id {} tried to update user with id {}", userIp, userAuth.getIdUser(), idAdmin);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Если тело запроса пустое
    if (adminSave == null) {
      log.warn("{} PUT \"/admins/{id}\": Request body \"adminSave\" is empty", userIp);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      adminService.update(idAdmin, adminSave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.error("PUT \"/admins/{id}\": {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (InvalidEmailException | AlreadyExistsException e) {
      // Строка в поле "email" не является email.
      // Администратор или пользователь с отличным ID и данным email уже существует
      log.error("{} PUT \"/admins/{id}\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает DELETE запрос "/admins/{id}" на удаление администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN".
   * Супер-администратор не может удалить у себя роль администратора. Для удаления, необходимо передать роль супер-администратора
   * @param idAdmin ID администратора
   * @param userAuth данные пользователя из jwt, отправившего запрос, для получения ID пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @DeleteMapping(
      value = "/admins/{id}")
  public ResponseEntity<?> delete(@PathVariable(name = "id") String idAdmin,
                                  @AuthenticationPrincipal JwtAuthenticationUserData userAuth,
                                  HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    // Если супер-администратор пытается удалить свою роль администратора
    if (userAuth.getIdUser().equals(idAdmin)) {
      log.warn("{} PUT \"/admins/{id}\": User with id {} tried to delete his role \"ROLE_SUPER_ADMIN\"", userIp, userAuth.getIdUser());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      adminService.delete(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.error("{} DELETE \"/admins/{id}\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/admins/from-user/{id}" на добавление роли "ROLE_ADMIN" пользователю.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idUser ID пользователя, которому добавляется роль
   * @param userAuth данные пользователя из jwt, отправившего запрос, для получения ID пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/admins/from-user/{id}")
  public ResponseEntity<?> fromUser(@PathVariable(name = "id") String idUser,
                                    @AuthenticationPrincipal JwtAuthenticationUserData userAuth,
                                    HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    // Если супер-администратор пытается добавить себе роль "ROLE_ADMIN"
    if (userAuth.getIdUser().equals(idUser)) {
      log.warn("{} POST \"/admins/from-user/{id}\": Super-admin already has role \"ROLE_ADMIN\"", userIp);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      adminService.fromUser(idUser);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден пользователь
      log.error("{} POST \"/admins/from-user/{id}\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (UserRoleException e) {
      // Пользователь имеет роль "ROLE_ADMIN" или не имеет роль "ROLE_USER"
      log.error("{} POST \"/admins/from-user/{id}\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/admins/{id}/to-user" на добавление роли "ROLE_USER" администратору.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idAdmin ID администратора, которому добавляется роль
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/admins/{id}/to-user")
  public ResponseEntity<?> toUser(@PathVariable(name = "id") String idAdmin,
                                  HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    try {
      adminService.toUser(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.error("{} POST \"/admins/{id}/to-user\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (UserRoleException e) {
      // Пользователь имеет роль "ROLE_USER" или не имеет роль "ROLE_ADMIN"
      log.error("{} POST \"/admins/{id}/to-user\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает POST запрос "/admins/{id}/to-super" на передачу роли "ROLE_SUPER_ADMIN" другому администратору.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_SUPER_ADMIN"
   * @param idAdmin ID администратора, которому передается роль
   * @param userAuth данные пользователя из jwt, отправившего запрос, для получения ID пользователя
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/admins/{id}/to-super")
  public ResponseEntity<?> toSuper(@PathVariable(name = "id") String idAdmin,
                                   @AuthenticationPrincipal JwtAuthenticationUserData userAuth,
                                   HttpServletRequest request) {

    String userIp = request.getRemoteAddr();

    // Если супер-администратор пытается передать себе роль "ROLE_SUPER_ADMIN"
    if (userAuth.getIdUser().equals(idAdmin)) {
      log.warn("{} POST \"/admins/{id}/to-super\": User already has role \"ROLE_SUPER_ADMIN\"", userIp);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      adminService.toSuper(idAdmin);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.error("{} POST \"/admins/{id}/to-super\": {}", userIp, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}

