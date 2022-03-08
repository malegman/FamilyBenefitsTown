package com.example.familybenefitstown.security.services.interfaces;

import com.example.familybenefitstown.dto.entity.ObjectEntity;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.NotFoundException;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Интерфейс сервиса, отвечающего за целостность базы данных
 */
public interface DBIntegrityService {

  /**
   * Проверяет существование в базе данных объекта по его ID
   * @param existFunc функция проверки, принимающая параметр типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param id ID проверяемого объекта
   * @throws NotFoundException если объект не найден
   */
  void checkExistenceById(Function<String, Boolean> existFunc, String id) throws NotFoundException;

  /**
   * Проверяет существование в базе данных объекта по его ID
   * @param existFunc функция проверки, принимающая параметр типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param entity проверяемый объект
   * @param <E> Тип проверяемого объекта
   * @throws NotFoundException если объект не найден
   */
  <E extends ObjectEntity> void checkExistenceById(Function<String, Boolean> existFunc, E entity) throws NotFoundException;

  /**
   * Проверяет отсутствие в базе данных объекта по его уникальному строковому полю
   * @param existFunc функция проверки, принимающая параметр типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param uniqueStr уникальное строковое поле объекта
   * @throws AlreadyExistsException если объект найден
   */
  void checkAbsenceByUniqStr(Function<String, Boolean> existFunc, String uniqueStr) throws AlreadyExistsException;

  /**
   * Проверяет отсутствие в базе данных объекта с отличным от данного ID с уникальным строковым полем
   * @param existBiFunc функция проверки, принимающая 2 параметра типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param idThis ID данного объекта
   * @param uniqueStr уникальное строковое поле объекта
   * @throws AlreadyExistsException если объект с отличным ID и данным строковым полем найден
   */
  void checkAbsenceAnotherByUniqStr(BiFunction<String, String, Boolean> existBiFunc, String idThis, String uniqueStr) throws AlreadyExistsException;

  /**
   * Подготавливает строку для вставки в SQL запрос, диалект PostgreSQL
   * @param content проверяемая строка
   * @return обработанная строка
   */
  String preparePostgreSQLString(String content);
}

