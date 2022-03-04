package com.example.familybenefitstown.service.impl;

import com.example.familybenefitstown.api_model.user.UserInfo;
import com.example.familybenefitstown.api_model.user.UserInitData;
import com.example.familybenefitstown.api_model.user.UserSave;
import com.example.familybenefitstown.convert.CityDBConverter;
import com.example.familybenefitstown.convert.UserDBConverter;
import com.example.familybenefitstown.dto.entity.ChildEntity;
import com.example.familybenefitstown.dto.entity.CityEntity;
import com.example.familybenefitstown.dto.entity.UserEntity;
import com.example.familybenefitstown.dto.repository.AccessTokenRepository;
import com.example.familybenefitstown.dto.repository.ChildRepository;
import com.example.familybenefitstown.dto.repository.CityRepository;
import com.example.familybenefitstown.dto.repository.UserRepository;
import com.example.familybenefitstown.exception.*;
import com.example.familybenefitstown.resource.R;
import com.example.familybenefitstown.security.service.inface.DBIntegrityService;
import com.example.familybenefitstown.security.service.inface.UserSecurityService;
import com.example.familybenefitstown.service.inface.DateTimeService;
import com.example.familybenefitstown.service.inface.EntityDBService;
import com.example.familybenefitstown.service.inface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, управляющего объектом "пользователь"
 */
@Service
public class UserServiceFB implements UserService {

  /**
   * Репозиторий, работающий с моделью таблицы "user"
   */
  private final UserRepository userRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "access_token"
   */
  private final AccessTokenRepository accessTokenRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "child"
   */
  private final ChildRepository childRepository;

  /**
   * Интерфейс сервиса модели таблицы "city", целостность которой зависит от связанных таблиц
   */
  private final EntityDBService<CityEntity, CityRepository> cityDBService;

  /**
   * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  private final DateTimeService dateTimeService;

  /**
   * Интерфейс сервиса, отвечающего за целостность базы данных
   */
  private final DBIntegrityService dbIntegrityService;
  /**
   * Интерфейс сервиса, отвечающего за данные пользователя
   */
  private final UserSecurityService userSecurityService;

  /**
   * Конструктор для инициализации интерфейсов репозиториев и сервисов
   * @param userRepository репозиторий, работающий с моделью таблицы "user"
   * @param accessTokenRepository репозиторий, работающий с моделью таблицы "access_token"
   * @param childRepository репозиторий, работающий с моделью таблицы "child"
   * @param cityDBService интерфейс сервиса модели таблицы "city", целостность которой зависит от связанных таблиц
   * @param dateTimeService интерфейс сервиса, который предоставляет методы для работы с датой и временем
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param userSecurityService интерфейс сервиса, отвечающего за данные пользователя
   */
  @Autowired
  public UserServiceFB(UserRepository userRepository,
                       AccessTokenRepository accessTokenRepository,
                       ChildRepository childRepository,
                       EntityDBService<CityEntity, CityRepository> cityDBService,
                       DateTimeService dateTimeService,
                       DBIntegrityService dbIntegrityService,
                       UserSecurityService userSecurityService) {
    this.userRepository = userRepository;
    this.accessTokenRepository = accessTokenRepository;
    this.childRepository = childRepository;
    this.cityDBService = cityDBService;
    this.dateTimeService = dateTimeService;
    this.dbIntegrityService = dbIntegrityService;
    this.userSecurityService = userSecurityService;
  }

  /**
   * Создает пользователя по запросу на сохранение. Регистрация гостя
   * @param userSave объект запроса на сохранение пользователя
   * @throws NotFoundException если город или критерии с указанными данными не найдены
   * @throws AlreadyExistsException если администратор или пользователь с указанным email уже существует
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws DateFormatException если даты рождения пользователя или детей не соответствуют формату "dd.mm.yyyy"
   * @throws DateTimeException если даты рождения пользователя или детей позже текущей даты
   */
  @Override
  public void create(UserSave userSave) throws
      NotFoundException,
      AlreadyExistsException,
      InvalidEmailException,
      DateFormatException,
      DateTimeException {

    // Проверка строки email на соответствие формату email
    userSecurityService.checkEmailElseThrow(
        userSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = UserDBConverter
        .fromSave(userSave, dbIntegrityService::preparePostgreSQLString);

    // Проверка существования города и критерий по их ID
    dbIntegrityService.checkExistenceById(
        cityDBService.getRepository()::existsById, userEntityFromSave.getCityEntity());

    // Проверка на существование пользователя или администратора по email
    dbIntegrityService.checkAbsenceByUniqStr(
        userRepository::existsByEmail, userEntityFromSave.getEmail());

    // Преобразование дат рождения пользователя и рождения детей
    userEntityFromSave.setDateBirth(dateTimeService.strToDate(userSave.getDateBirth()));
    userEntityFromSave.setChildEntitySet(strBirthSetToChildEntity(userSave.getBirthDateChildren()));

    // Проверка дат рождения пользователя и детей на предшествие текущей даты
    dateTimeService.checkDateBeforeNow(userEntityFromSave.getDateBirth());
    dateTimeService.checkDateBeforeNow(userEntityFromSave.getChildEntitySet()
                                           .stream().map(ChildEntity::getDateBirth).collect(Collectors.toSet()));

    userEntityFromSave.addRole(R.ROLE_USER);
    userEntityFromSave.setDateSelectCriterion(LocalDate.from(Instant.now()));
    userEntityFromSave.setFreshBenefits(false);

    userRepository.saveAndFlush(userEntityFromSave);
  }

  /**
   * Возвращает пользователя об учреждении по его ID
   * @param idUser ID пользователя
   * @return информация о пользователе
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  @Override
  public UserInfo read(String idUser) throws NotFoundException {

    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);

    // Получение пользователя по его ID, если пользователь существует
    UserEntity userEntityFromRequest = userRepository.findById(prepareIdUser)
        .orElseThrow(() -> new NotFoundException(String.format(
            "User with ID \"%s\" not found", idUser)));

    return UserDBConverter.toInfo(userEntityFromRequest);
  }

  /**
   * Обновляет пользователя по запросу на обновление
   * @param idUser ID пользователя
   * @param userSave объект запроса на сохранение пользователя
   * @throws NotFoundException если пользователь, город или критерии с указанными данными не найдены
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws DateFormatException если даты рождения пользователя или детей не соответствуют формату "dd.mm.yyyy"
   * @throws DateTimeException если даты рождения пользователя или детей позже текущей даты
   * @throws AlreadyExistsException если пользователь с отличным ID и данным email уже существует
   */
  @Override
  public void update(String idUser, UserSave userSave) throws
      NotFoundException,
      InvalidEmailException,
      DateFormatException,
      DateTimeException,
      AlreadyExistsException {

    // Проверка строки email на соответствие формату email
    userSecurityService.checkEmailElseThrow(
        userSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = UserDBConverter
        .fromSave(userSave, dbIntegrityService::preparePostgreSQLString);

    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);

    // Проверка существования города и критерий по их ID
    dbIntegrityService.checkExistenceById(
        cityDBService.getRepository()::existsById, userEntityFromSave.getCityEntity());

    // Проверка отсутствия пользователя с отличным от данного ID и данным email
    dbIntegrityService.checkAbsenceAnotherByUniqStr(
        userRepository::existsByIdIsNotAndName, prepareIdUser, userEntityFromSave.getEmail());

    // Получение пользователя по его ID, если пользователь существует
    UserEntity userEntityFromDB = userRepository.findById(prepareIdUser)
        .orElseThrow(() -> new NotFoundException(String.format(
            "User with ID \"%s\" not found", idUser)));

    // Преобразование дат рождения пользователя и рождения детей
    userEntityFromDB.setDateBirth(dateTimeService.strToDate(
        userSave.getDateBirth()));
    userEntityFromDB.setChildEntitySet(strBirthSetToChildEntity(
        userSave.getBirthDateChildren()));

    // Проверка дат рождения пользователя и детей на предшествие текущей даты
    dateTimeService.checkDateBeforeNow(
        userEntityFromDB.getDateBirth());
    dateTimeService.checkDateBeforeNow(
        userEntityFromDB.getChildEntitySet().stream()
            .map(ChildEntity::getDateBirth).collect(Collectors.toSet()));

    userEntityFromDB.setEmail(userEntityFromSave.getEmail());
    userEntityFromDB.setName(userEntityFromSave.getName());
    userEntityFromDB.setDateSelectCriterion(LocalDate.from(Instant.now()));
    userEntityFromDB.setFreshBenefits(true);

    userRepository.saveAndFlush(userEntityFromDB);
  }

  /**
   * Удаляет пользователя по его ID или удаляет роль "ROLE_USER" у администратора
   * @param idUser ID пользователя
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  @Override
  public void delete(String idUser) throws NotFoundException {

    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);

    // Получение пользователя по его ID, если пользователь существует
    UserEntity userEntityFromRequest = userRepository.findById(prepareIdUser)
        .orElseThrow(() -> new NotFoundException(String.format(
            "User with ID \"%s\" not found", idUser)));

    // Если есть роль "ROLE_ADMIN", удаление роли "ROLE_USER", иначе удаление пользователя и его токена доступа
    if (userEntityFromRequest.hasRole(R.ROLE_ADMIN)) {
      userEntityFromRequest.removeRole(R.ROLE_USER);
      userRepository.saveAndFlush(userEntityFromRequest);
    } else {
      userRepository.deleteById(prepareIdUser);
      accessTokenRepository.deleteById(prepareIdUser);
    }
  }

  /**
   * Возвращает дополнительные данные для пользователя.
   * Данные содержат в себе множества кратких информаций о городах и полных критериях
   * @return дополнительные данные для пользователя
   */
  @Override
  public UserInitData getInitData() {

    return UserInitData
        .builder()
        .shortCitySet(cityDBService
                          .findAllFull()
                          .stream()
                          .map(CityDBConverter::toShortInfo)
                          .collect(Collectors.toSet()))
        .build();
  }

  /**
   * Преобразует множество строк формата "dd.mm.yyyy" с датами рождения детей в модели таблицы "child"
   * @param strBirthSet множество строк формата "dd.mm.yyyy" с датами рождения детей
   * @return множество моделей таблицы "child"
   * @throws DateFormatException если одна из полученных строк не соответствует формату "dd.mm.yyyy"
   */
  private Set<ChildEntity> strBirthSetToChildEntity(Set<String> strBirthSet) throws DateFormatException {

    Set<LocalDate> dateBirthSet = new HashSet<>();

    // Преобразование строк формата "dd.mm.yyyy" в дату
    for (String strBirth : strBirthSet) {
      dateBirthSet.add(dateTimeService.strToDate(strBirth));
    }

    // Преобразование дат рождения в модели детей.
    // Если модели ребенка с указанной датой нет, то создается новая модель с указанной датой
    return dateBirthSet
        .stream()
        .map(dateBirth -> childRepository.findByDateBirth(dateBirth)
            .orElseGet(() -> {
              childRepository.saveAndFlush(new ChildEntity(dateBirth));
              return childRepository.getByDateBirth(dateBirth);
            }))
        .collect(Collectors.toSet());
  }
}
