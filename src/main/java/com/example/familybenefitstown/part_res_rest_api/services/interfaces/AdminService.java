package com.example.familybenefitstown.part_res_rest_api.services.interfaces;

import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminSave;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;

/**
 * Интерфейс сервиса, управляющего объектом "администратор"
 */
public interface AdminService {

  /**
   * Возвращает администратора по его ID
   * @param idAdmin ID администратора
   * @return информация об администраторе
   * @throws NotFoundException если администратор с данным ID не найден
   */
  AdminInfo read(String idAdmin) throws NotFoundException;

  /**
   * Обновляет администратора по запросу на сохранение
   * @param idAdmin ID администратора
   * @param adminSave объект запроса на сохранение администратора
   * @throws NotFoundException если администратор с указанными данными не найден
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws AlreadyExistsException если администратор или пользователь с отличным ID и данным email уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  void update(String idAdmin, AdminSave adminSave) throws NotFoundException, InvalidEmailException, AlreadyExistsException, InvalidStringException;
}

