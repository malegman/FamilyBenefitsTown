package com.example.familybenefitstown.security.services.implementations;

import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import org.springframework.stereotype.Service;

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

