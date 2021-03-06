package com.example.familybenefitstown.part_res_rest_api.services.implementations;

import com.example.familybenefitstown.dto.entities.CityEntity;
import com.example.familybenefitstown.dto.repositories.CityRepository;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_res_rest_api.api_models.city.CityInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.city.CitySave;
import com.example.familybenefitstown.part_res_rest_api.api_models.common.ObjectShortInfo;
import com.example.familybenefitstown.part_res_rest_api.converters.CityDBConverter;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.CityService;
import com.example.familybenefitstown.security.DBSecuritySupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, управляющего объектом "город"
 */
@Slf4j
@Service
public class CityServiceFB implements CityService {

  /**
   * Репозиторий, работающий с моделью таблицы "city"
   */
  private final CityRepository cityRepository;

  /**
   * Конструктор для инициализации интерфейсов репозиториев и сервиса
   * @param cityRepository репозиторий, работающий с моделью таблицы "city"
   */
  @Autowired
  public CityServiceFB(CityRepository cityRepository) {
    this.cityRepository = cityRepository;
  }

  /**
   * Возвращает множество городов.
   * Фильтр по названию города.
   * В качестве параметра может быть указан {@code null}, если данный параметр не участвует в фильтрации
   * @param nameCity Название города
   * @return множество кратких информаций о городах
   */
  @Override
  public List<ObjectShortInfo> readAllFilter(String nameCity) {

    return cityRepository.findAll()
        .stream()
        .filter(cityEntity -> nameCity == null || cityEntity.getName().equals(nameCity))
        .map(CityDBConverter::toShortInfo)
        .collect(Collectors.toList());
  }

  /**
   * Создает город по запросу на сохранение
   * @param citySave объект запроса на сохранение города
   * @throws AlreadyExistsException если город с указанным названием уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  @Override
  public void create(CitySave citySave) throws AlreadyExistsException, InvalidStringException {

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    CityEntity cityEntityFromSave = CityDBConverter
        .fromSave(null, citySave, DBSecuritySupport::preparePostgreSQLString);

    // Проверка отсутствия города по его названию
    DBSecuritySupport.checkAbsenceByUniqStr(
        cityRepository::existsByName, cityEntityFromSave.getName());

    cityRepository.save(cityEntityFromSave);
    log.info("DB. City with name \"{}\" created.", citySave.getName());
  }

  /**
   * Возвращает информацию о городе по его ID
   * @param idCity ID города
   * @return информация о городе
   * @throws NotFoundException если город с указанным ID не найден
   */
  @Override
  public CityInfo read(String idCity) throws NotFoundException {

    // Получение города по его ID, если город существует
    String prepareIdCity = DBSecuritySupport.preparePostgreSQLString(idCity);
    CityEntity cityEntityFromRequest = cityRepository.findById(prepareIdCity).orElseThrow(
        () -> new NotFoundException(String.format("City with ID \"%s\" not found", idCity)));

    return CityDBConverter.toInfo(cityEntityFromRequest);
  }

  /**
   * Обновляет город по запросу на сохранение
   * @param idCity ID города
   * @param citySave объект запроса на сохранение города
   * @throws NotFoundException если город с указанным ID не найден
   * @throws AlreadyExistsException если город с отличным ID и данным названием уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  @Override
  public void update(String idCity, CitySave citySave) throws NotFoundException, AlreadyExistsException, InvalidStringException {

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    CityEntity cityEntityFromSave = CityDBConverter
        .fromSave(idCity, citySave, DBSecuritySupport::preparePostgreSQLString);

    String prepareIdCity = cityEntityFromSave.getId();

    // Проверка существование города по его ID
    DBSecuritySupport.checkExistenceById(
        cityRepository::existsById, prepareIdCity);

    // Проверка отсутствия города с отличным от данного ID и данным названием
    DBSecuritySupport.checkAbsenceAnotherByUniqStr(
        cityRepository::existsByIdIsNotAndName, prepareIdCity, cityEntityFromSave.getName());

    cityRepository.save(cityEntityFromSave);
    log.info("DB. City with ID \"{}\" updated.", idCity);
  }

  /**
   * Удаляет город по его ID
   * @param idCity ID города
   * @throws NotFoundException если город с указанным ID не найден
   */
  @Override
  public void delete(String idCity) throws NotFoundException {

    String prepareIdCity = DBSecuritySupport.preparePostgreSQLString(idCity);

    // Проверка существование города по его ID
    DBSecuritySupport.checkExistenceById(
        cityRepository::existsById, prepareIdCity);

    cityRepository.deleteById(prepareIdCity);
    log.info("DB. City with ID \"{}\" deleted.", idCity);
  }
}

