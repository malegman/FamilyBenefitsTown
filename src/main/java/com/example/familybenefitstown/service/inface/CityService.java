package com.example.familybenefitstown.service.inface;

import com.example.familybenefitstown.api_model.city.CityInfo;
import com.example.familybenefitstown.api_model.city.CitySave;
import com.example.familybenefitstown.api_model.common.ObjectShortInfo;
import com.example.familybenefitstown.exception.AlreadyExistsException;
import com.example.familybenefitstown.exception.NotFoundException;

import java.util.Set;

/**
 * Интерфейс сервиса, управляющего объектом "город"
 */
public interface CityService {

  /**
   * Возвращает множество городов, в которых есть учреждения и пособия.
   * Фильтр по названию города и пособию.
   * В качестве параметра может быть указан null, если данный параметр не участвует в фильтрации
   * @param nameCity Название города
   * @param idBenefit ID пособия
   * @return множество кратких информаций о городах
   */
  Set<ObjectShortInfo> readAllFilter(String nameCity, String idBenefit);

  /**
   * Создает город по запросу на сохранение
   * @param citySave объект запроса на сохранение города
   * @throws AlreadyExistsException если город с указанным названием уже существует
   * @throws NotFoundException если пособие города с указанным ID не найдено
   */
  void create(CitySave citySave) throws AlreadyExistsException, NotFoundException;

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
   */
  void update(String idCity, CitySave citySave) throws NotFoundException, AlreadyExistsException;

  /**
   * Удаляет город по его ID
   * @param idCity ID города
   * @throws NotFoundException если город с указанным ID не найден
   */
  void delete(String idCity) throws NotFoundException;

  /**
   * Возвращает множество городов, в которых нет учреждений или пособий
   * @return множество кратких информаций о городах
   */
  Set<ObjectShortInfo> readAllPartial();
}

