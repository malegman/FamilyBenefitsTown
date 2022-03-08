package com.example.familybenefitstown.services.interfaces;

import com.example.familybenefitstown.api_models.admin.AdminInfo;
import com.example.familybenefitstown.api_models.admin.AdminSave;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.NotFoundException;

/**
 * Интерфейс сервиса, управляющего объектом "администратор"
 */
public interface AdminService {

  /**
   * Возвращает администратора по его ID
   * @param idAdmin ID администратора
   * @return информация об администраторе
   */
  AdminInfo read(String idAdmin) throws NotFoundException;

  /**
   * Обновляет администратора по запросу на сохранение
   * @param idAdmin ID администратора
   * @param adminSave объект запроса на сохранение администратора
   * @throws NotFoundException если администратор с указанными данными не найден
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws AlreadyExistsException если администратор или пользователь с отличным ID и данным email уже существует
   */
  void update(String idAdmin, AdminSave adminSave) throws NotFoundException, InvalidEmailException, AlreadyExistsException;
}

