package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.api_models.city.CityInfo;
import com.example.familybenefitstown.api_models.city.CitySave;
import com.example.familybenefitstown.api_models.common.ObjectShortInfo;
import com.example.familybenefitstown.converters.CityDBConverter;
import com.example.familybenefitstown.dto.entities.CityEntity;
import com.example.familybenefitstown.dto.repositories.CityRepository;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.security.generator.RandomValue;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.services.interfaces.CityService;
import com.example.familybenefitstown.services.interfaces.EntityDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, управляющего объектом "город"
 */
@Slf4j
@Service
public class CityServiceFB implements CityService, EntityDBService<CityEntity, CityRepository> {

  /**
   * Репозиторий, работающий с моделью таблицы "city"
   */
  private final CityRepository cityRepository;

  /**
   * Интерфейс сервиса, отвечающего за целостность базы данных
   */
  private final DBIntegrityService dbIntegrityService;

  /**
   * Конструктор для инициализации интерфейсов репозиториев и сервиса
   * @param cityRepository репозиторий, работающий с моделью таблицы "city"
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   */
  @Autowired
  public CityServiceFB(CityRepository cityRepository,
                       DBIntegrityService dbIntegrityService) {
    this.cityRepository = cityRepository;
    this.dbIntegrityService = dbIntegrityService;
  }

  /**
   * Возвращает множество городов.
   * Фильтр по названию города.
   * В качестве параметра может быть указан {@code null}, если данный параметр не участвует в фильтрации
   * @param nameCity Название города
   * @return множество кратких информаций о городах
   */
  @Override
  public Set<ObjectShortInfo> readAllFilter(String nameCity) {

    return findAllFull()
        .stream()
        .filter(cityEntity -> nameCity == null || cityEntity.getName().equals(nameCity))
        .map(CityDBConverter::toShortInfo)
        .collect(Collectors.toSet());
  }

  /**
   * Создает город по запросу на сохранение
   * @param citySave объект запроса на сохранение города
   * @throws AlreadyExistsException если город с указанным названием уже существует
   * @throws NotFoundException если пособие города с указанным ID не найдено
   */
  @Override
  public void create(CitySave citySave) throws AlreadyExistsException, NotFoundException {

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    CityEntity cityEntityFromSave = CityDBConverter
        .fromSave(citySave, dbIntegrityService::preparePostgreSQLString);

    // Проверка отсутствия города по его названию
    dbIntegrityService.checkAbsenceByUniqStr(
        cityRepository::existsByName, cityEntityFromSave.getName());

    cityEntityFromSave.setId(RandomValue.randomString(R.ID_LENGTH));

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
    String prepareIdCity = dbIntegrityService.preparePostgreSQLString(idCity);
    CityEntity cityEntityFromRequest = cityRepository.findById(prepareIdCity)
        .orElseThrow(() -> new NotFoundException(String.format(
            "City with ID \"%s\" not found", idCity)));

    return CityDBConverter.toInfo(cityEntityFromRequest);
  }

  /**
   * Обновляет город по запросу на сохранение
   * @param idCity ID города
   * @param citySave объект запроса на сохранение города
   * @throws NotFoundException если город с указанным ID не найден
   * @throws AlreadyExistsException если город с отличным ID и данным названием уже существует
   */
  @Override
  public void update(String idCity, CitySave citySave) throws NotFoundException, AlreadyExistsException {

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    CityEntity cityEntityFromSave = CityDBConverter
        .fromSave(citySave, dbIntegrityService::preparePostgreSQLString);

    String prepareIdCity = dbIntegrityService.preparePostgreSQLString(idCity);

    // Проверка существование города по его ID
    dbIntegrityService.checkExistenceById(
        cityRepository::existsById, prepareIdCity);

    // Проверка отсутствия города с отличным от данного ID и данным названием
    dbIntegrityService.checkAbsenceAnotherByUniqStr(
        cityRepository::existsByIdIsNotAndName, prepareIdCity, cityEntityFromSave.getName());

    cityEntityFromSave.setId(prepareIdCity);

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

    String prepareIdCity = dbIntegrityService.preparePostgreSQLString(idCity);

    // Проверка существование города по его ID
    dbIntegrityService.checkExistenceById(
        cityRepository::existsById, prepareIdCity);

    cityRepository.deleteById(prepareIdCity);
    log.info("DB. City with ID \"{}\" deleted.", idCity);
  }

  /**
   * Возвращает репозиторий сервиса
   * @return репозиторий сервиса
   */
  @Override
  public CityRepository getRepository() {
    return cityRepository;
  }

  /**
   * Возвращает множество всех моделей таблицы "city"
   * @return множество моделей таблиц
   */
  @Override
  public Set<CityEntity> findAllFull() {

    return new HashSet<>(cityRepository.findAll());
  }

  /**
   * Возвращает множество моделей таблицы "city", в которых нет моделей пособий или учреждений
   * @return множество моделей таблиц
   */
  @Override
  public Set<CityEntity> findAllPartial() {

    return new HashSet<>(cityRepository.findAll());
  }
}

