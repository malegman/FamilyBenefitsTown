package com.example.familybenefitstown.res_part_rest_api.services.interfaces;

import com.example.familybenefitstown.exceptions.*;
import com.example.familybenefitstown.res_part_rest_api.api_models.user.UserInfo;
import com.example.familybenefitstown.res_part_rest_api.api_models.user.UserInitData;
import com.example.familybenefitstown.res_part_rest_api.api_models.user.UserSave;

/**
 * Интерфейс сервиса, управляющего объектом "пользователь"
 */
public interface UserService {

  /**
   * Создает пользователя по запросу на сохранение. Регистрация гостя
   * @param userSave объект запроса на сохранение пользователя
   * @throws NotFoundException если город или критерии с указанными данными не найдены
   * @throws AlreadyExistsException если администратор или пользователь с указанным email уже существует
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws DateFormatException если даты рождения пользователя или детей не соответствуют формату "dd.mm.yyyy"
   * @throws DateTimeException если даты рождения пользователя или детей позже текущей даты
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  void create(UserSave userSave) throws
      NotFoundException,
      AlreadyExistsException,
      InvalidEmailException,
      DateFormatException,
      DateTimeException,
      InvalidStringException;

  /**
   * Возвращает пользователя об учреждении по его ID
   * @param idUser ID пользователя
   * @return информация о пользователе
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  UserInfo read(String idUser) throws NotFoundException;

  /**
   * Обновляет пользователя по запросу на обновление
   * @param idUser ID пользователя
   * @param userSave объект запроса на сохранение пользователя
   * @throws NotFoundException если пользователь, город или критерии с указанными данными не найдены
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws DateFormatException если даты рождения пользователя или детей не соответствуют формату "dd.mm.yyyy"
   * @throws DateTimeException если даты рождения пользователя или детей позже текущей даты
   * @throws AlreadyExistsException если пользователь с отличным ID и данным email уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  void update(String idUser, UserSave userSave) throws
      NotFoundException,
      InvalidEmailException,
      DateFormatException,
      DateTimeException,
      AlreadyExistsException,
      InvalidStringException;

  /**
   * Удаляет пользователя по его ID или удаляет роль "ROLE_USER" у администратора
   * @param idUser ID пользователя
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  void delete(String idUser) throws NotFoundException;

  /**
   * Возвращает дополнительные данные для пользователя.
   * Данные содержат в себе множества кратких информаций о городах
   * @return дополнительные данные для пользователя
   */
  UserInitData getInitData();

  /**
   * Проверяет существование пользователя по email
   * @param email почта пользователя
   * @return {@code true}, если пользователь существует
   */
  boolean existsByEmail(String email);
}

