package com.example.familybenefitstown.part_res_rest_api.services.interfaces;

import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminSave;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;

/**
 * Интерфейс сервиса, управляющего объектом "супер-администратор"
 */
public interface SuperAdminService {

  /**
   * Создает администратора по запросу на сохранение
   * @param adminSave объект запроса на сохранение администратора
   * @throws AlreadyExistsException если администратор или пользователь с указанным email уже существует
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  void create(AdminSave adminSave) throws AlreadyExistsException, InvalidEmailException, InvalidStringException;

  /**
   * Удаляет администратора по его ID или удаляет роль "ROLE_ADMIN" у пользователя
   * @param idAdmin ID администратора
   * @throws NotFoundException если администратор с указанным ID не найден
   */
  void delete(String idAdmin) throws NotFoundException;

  /**
   * Добавляет роль "ROLE_ADMIN" пользователю
   * @param idUser ID пользователя
   * @throws NotFoundException если пользователь с данным ID и ролью "ROLE_USER" не найден
   * @throws AlreadyExistsException если пользователь имеет роль "ROLE_ADMIN"
   */
  void fromUser(String idUser) throws NotFoundException, AlreadyExistsException;

  /**
   * Добавляет роль "ROLE_USER" администратору
   * @param idAdmin ID администратора
   * @throws NotFoundException если пользователь с данным ID и ролью "ROLE_ADMIN" не найден
   * @throws AlreadyExistsException если пользователь имеет роль "ROLE_USER"
   */
  void toUser(String idAdmin) throws NotFoundException, AlreadyExistsException;

  /**
   * Передает роль "ROLE_SUPER_ADMIN" указанному администратору, удаляя данную роль у текущего администратора
   * @param idAdmin ID администратора
   * @throws NotFoundException если администратор с данным ID не найден
   */
  void toSuper(String idAdmin) throws NotFoundException;
}
