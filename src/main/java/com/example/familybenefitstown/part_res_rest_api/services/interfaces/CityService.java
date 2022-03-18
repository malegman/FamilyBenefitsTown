package com.example.familybenefitstown.part_res_rest_api.services.interfaces;

import com.example.familybenefitstown.part_res_rest_api.api_models.city.CityInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.city.CitySave;
import com.example.familybenefitstown.part_res_rest_api.api_models.common.ObjectShortInfo;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;

import java.util.Set;

/**
 * Интерфейс сервиса, управляющего объектом "город"
 */
public interface CityService {

  /**
   * Возвращает множество городов.
   * Фильтр по названию города.
   * В качестве параметра может быть указан {@code null}, если данный параметр не участвует в фильтрации
   * @param nameCity Название города
   * @return множество кратких информаций о городах
   */
  Set<ObjectShortInfo> readAllFilter(String nameCity);

  /**
   * Создает город по запросу на сохранение
   * @param citySave объект запроса на сохранение города
   * @throws AlreadyExistsException если город с указанным названием уже существует
   * @throws NotFoundException если пособие города с указанным ID не найдено
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  void create(CitySave citySave) throws AlreadyExistsException, NotFoundException, InvalidStringException;

  /**
   * Возвращает информацию о городе по его ID
   * @param idCity ID города
   * @return информация о городе
   * @throws NotFoundException если город с указанным ID не найден
   */
  CityInfo read(String idCity) throws NotFoundException;

  /**
   * Обновляет город по запросу на сохранение
   * @param idCity ID города
   * @param citySave объект запроса на сохранение города
   * @throws NotFoundException если город с указанным ID не найден
   * @throws AlreadyExistsException если город с отличным ID и данным названием уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  void update(String idCity, CitySave citySave) throws NotFoundException, AlreadyExistsException, InvalidStringException;

  /**
   * Удаляет город по его ID
   * @param idCity ID города
   * @throws NotFoundException если город с указанным ID не найден
   */
  void delete(String idCity) throws NotFoundException;
}

