package com.example.familybenefitstown.security.service.impl;

import com.example.familybenefitstown.dto.entity.ObjectEntity;
import com.example.familybenefitstown.exception.AlreadyExistsException;
import com.example.familybenefitstown.exception.NotFoundException;
import com.example.familybenefitstown.security.service.inface.DBIntegrityService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Реализация сервиса, отвечающего за целостность базы данных
 */
@Service
public class DBIntegrityServiceFB implements DBIntegrityService {

  /**
   * Проверяет существование в базе данных объекта по его ID
   * @param existFunc функция проверки, принимающая параметр типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param id ID проверяемого объекта
   * @throws NotFoundException если объект не найден
   */
  @Override
  public void checkExistenceById(Function<String, Boolean> existFunc, String id) throws NotFoundException {

    if (!existFunc.apply(id)) {
      throw new NotFoundException(String.format(
          "Entity with ID \"%s\" not found in repository %s", id, existFunc.getClass().getName()));
    }
  }

  /**
   * Проверяет существование в базе данных объекта по его ID
   * @param existFunc функция проверки, принимающая параметр типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param entity проверяемый объект
   * @param <E> Тип проверяемого объекта
   * @throws NotFoundException если объект не найден
   */
  @Override
  public <E extends ObjectEntity> void checkExistenceById(Function<String, Boolean> existFunc, E entity) throws NotFoundException {

    checkExistenceById(existFunc, entity.getId());
  }

  /**
   * Проверяет существование в базе данных объекта из множества по ID
   * @param existFunc функция проверки, принимающая параметр типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param entitySet множество проверяемых объектов
   * @param <E> Тип проверяемого объекта в множестве
   * @throws NotFoundException если объект из множества не найден
   */
  @Override
  public <E extends ObjectEntity> void checkExistenceById(Function<String, Boolean> existFunc, Set<E> entitySet) throws NotFoundException {

    for (E entity : entitySet) {
      checkExistenceById(existFunc, entity.getId());
    }
  }

  /**
   * Проверяет существование в базе данных объекта из множества по его уникальному строковому полю
   * @param existFunc функция проверки, принимающая параметр типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param uniqueStr уникальное строковое поле объекта
   * @throws NotFoundException если объект не найден
   */
  @Override
  public void checkExistenceByUniqStr(Function<String, Boolean> existFunc, String uniqueStr) throws NotFoundException {

    if (!existFunc.apply(uniqueStr)) {
      throw new NotFoundException(String.format(
          "Entity with ID \"%s\" not found in repository %s", uniqueStr, existFunc.getClass().getName()));
    }
  }

  /**
   * Проверяет отсутствие в базе данных объекта по его уникальному строковому полю
   * @param existFunc функция проверки, принимающая параметр типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param uniqueStr уникальное строковое поле объекта
   * @throws AlreadyExistsException если объект найден
   */
  @Override
  public void checkAbsenceByUniqStr(Function<String, Boolean> existFunc, String uniqueStr) throws AlreadyExistsException {

    if (existFunc.apply(uniqueStr)) {
      throw new AlreadyExistsException(String.format(
          "Entity with field \"%s\" already exists in repository %s", uniqueStr, existFunc.getClass().getName()));
    }
  }

  /**
   * Проверяет отсутствие в базе данных объекта с отличным от данного ID с уникальным строковым полем
   * @param existBiFunc функция проверки, принимающая 2 параметра типа {@link String} и возвращающая значение типа {@link Boolean}
   * @param idThis ID данного объекта
   * @param uniqueStr уникальное строковое поле объекта
   * @throws AlreadyExistsException если объект с отличным ID и данным строковым полем найден
   */
  @Override
  public void checkAbsenceAnotherByUniqStr(BiFunction<String, String, Boolean> existBiFunc, String idThis, String uniqueStr) throws AlreadyExistsException {

    if (existBiFunc.apply(idThis, uniqueStr)) {
      throw new AlreadyExistsException(String.format(
          "Entity with field \"%s\" already exists in repository %s", uniqueStr, existBiFunc.getClass().getName()));
    }
  }

  /**
   * Подготавливает строку для вставки в SQL запрос, диалект PostgreSQL
   * @param content проверяемая строка
   * @return обработанная строка
   */
  @Override
  public String preparePostgreSQLString(String content) {

    if (content == null) {
      return null;
    }

    return content.replace("'", "''");
  }
}

