package com.example.familybenefitstown.part_res_rest_api.controllers;

import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminSave;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер запросов, связанных с администратором
 */
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
   * @return информация об администраторе, если запрос выполнен успешно, и код ответа
   * @throws NotFoundException если администратор с данным ID не найден
   */
  @GetMapping(
      value = "/api/admins/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<AdminInfo> read(@PathVariable(name = "id") String idAdmin) throws NotFoundException {

    AdminInfo adminInfo = adminService.read(idAdmin);
    return ResponseEntity.status(HttpStatus.OK).body(adminInfo);
  }

  /**
   * Обрабатывает PUT запрос "/api/admins/{id}" на обновление администратора.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_ADMIN".
   * Администратор может обновить только свой профиль.
   * @param idAdmin ID администратора
   * @param adminSave объект запроса для сохранения администратора
   * @return код ответа, результат обработки запроса
   * @throws AlreadyExistsException если администратор или пользователь с отличным ID и данным email уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   * @throws NotFoundException если администратор с указанными данными не найден
   * @throws InvalidEmailException если указанный "email" не является email
   */
  @PutMapping(
      value = "/api/admins/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> update(@PathVariable(name = "id") String idAdmin, @RequestBody AdminSave adminSave)
      throws AlreadyExistsException, InvalidStringException, NotFoundException, InvalidEmailException {

    adminService.update(idAdmin, adminSave);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
