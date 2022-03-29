package com.example.familybenefitstown.part_res_rest_api.controllers;

import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminSave;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
   * Обрабатывает GET запрос "/api/admins/{id}" на получение информации об администраторе.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_ADMIN".
   * Администратор может получить информацию только о своем профиле.
   * @param idAdmin ID администратора
   * @param request http запрос
   * @return информация об администраторе, если запрос выполнен успешно, и код ответа
   */
  @GetMapping(
      value = "/api/admins/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<AdminInfo> read(@PathVariable(name = "id") String idAdmin,
                                        HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} GET \"/api/admins/{}\": Request in controller", requestAddress, idAdmin);

    try {
      AdminInfo adminInfo = adminService.read(idAdmin);
      return ResponseEntity.status(HttpStatus.OK).body(adminInfo);

    } catch (NotFoundException e) {
      // Не найден администратор
      log.warn("{} GET \"/api/admins/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает PUT запрос "/api/admins/{id}" на обновление администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_ADMIN".
   * Администратор может обновить только свой профиль.
   * @param idAdmin ID администратора
   * @param adminSave объект запроса для сохранения администратора
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PutMapping(
      value = "/api/admins/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> update(@PathVariable(name = "id") String idAdmin,
                                  @RequestBody AdminSave adminSave,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/api/admins/{}\": Request in controller", requestAddress, idAdmin);

    // Если тело запроса пустое
    if (adminSave == null) {
      log.warn("{} PUT \"/api/admins/{}\": Request body \"adminSave\" is empty", requestAddress, idAdmin);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      adminService.update(idAdmin, adminSave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден администратор
      log.warn("{} PUT \"/api/admins/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (InvalidEmailException | AlreadyExistsException | InvalidStringException e) {
      // Строка в поле "email" не является email.
      // Администратор или пользователь с отличным ID и данным email уже существует.
      // Некорректное строковое поле объекта запроса.
      log.warn("{} PUT \"/api/admins/{}\": {}", requestAddress, idAdmin, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}

