package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.api_models.user.UserInfo;
import com.example.familybenefitstown.api_models.user.UserInitData;
import com.example.familybenefitstown.api_models.user.UserSave;
import com.example.familybenefitstown.converters.CityDBConverter;
import com.example.familybenefitstown.converters.UserDBConverter;
import com.example.familybenefitstown.dto.entities.strong.CityEntity;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.dto.repositories.strong.CityRepository;
import com.example.familybenefitstown.dto.repositories.strong.UserRepository;
import com.example.familybenefitstown.exceptions.*;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.resources.RDB;
import com.example.familybenefitstown.security.generator.RandomValue;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.security.services.interfaces.TokenCodeService;
import com.example.familybenefitstown.services.interfaces.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, управляющего объектом "пользователь"
 */
@Slf4j
@Service
public class UserServiceFB implements UserService {

  /**
   * Репозиторий, работающий с моделью таблицы "user"
   */
  private final UserRepository userRepository;

  /**
   * Интерфейс сервиса, управляющего связью пользователей и ролей
   */
  private final UsersRolesService usersRolesService;
  /**
   * Интерфейс сервиса, управляющего связью пользователей и детей
   */
  private final UsersChildrenService usersChildrenService;

  /**
   * Интерфейс сервиса модели таблицы "city", целостность которой зависит от связанных таблиц
   */
  private final EntityDBService<CityEntity, CityRepository> cityDBService;

  /**
   * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  private final DateTimeService dateTimeService;

  /**
   * Интерфейс сервиса для работы с токенами доступа (в формате jwt) и восстановления и кодом для входа
   */
  private final TokenCodeService tokenCodeService;
  /**
   * Интерфейс сервиса, отвечающего за целостность базы данных
   */
  private final DBIntegrityService dbIntegrityService;
  /**
   * Интерфейс сервиса для отправки сообщений на электронную почту
   */
  private final MailService mailService;

  /**
   * Конструктор для инициализации интерфейсов репозиториев и сервисов
   * @param userRepository репозиторий, работающий с моделью таблицы "user"
   * @param usersRolesService интерфейс сервиса, управляющего связью пользователей и ролей
   * @param usersChildrenService интерфейс сервиса, управляющего связью пользователей и детей
   * @param cityDBService интерфейс сервиса модели таблицы "city", целостность которой зависит от связанных таблиц
   * @param dateTimeService интерфейс сервиса, который предоставляет методы для работы с датой и временем
   * @param tokenCodeService интерфейс сервиса для работы с токенами доступа (в формате jwt) и восстановления и кодом для входа
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param mailService интерфейс сервиса для отправки сообщений на электронную почту
   */
  @Autowired
  public UserServiceFB(UserRepository userRepository,
                       UsersRolesService usersRolesService,
                       UsersChildrenService usersChildrenService,
                       EntityDBService<CityEntity, CityRepository> cityDBService,
                       DateTimeService dateTimeService,
                       TokenCodeService tokenCodeService,
                       DBIntegrityService dbIntegrityService,
                       MailService mailService) {
    this.userRepository = userRepository;
    this.usersRolesService = usersRolesService;
    this.usersChildrenService = usersChildrenService;
    this.tokenCodeService = tokenCodeService;
    this.cityDBService = cityDBService;
    this.dateTimeService = dateTimeService;
    this.dbIntegrityService = dbIntegrityService;
    this.mailService = mailService;
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
    mailService.checkEmailElseThrow(userSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = UserDBConverter
        .fromSave(userSave, dbIntegrityService::preparePostgreSQLString);

    // Проверка существования города по ID
    dbIntegrityService.checkExistenceById(
        cityDBService.getRepository()::existsById, userEntityFromSave.getCityEntity());

    // Проверка на отсутствие пользователя или администратора по email
    dbIntegrityService.checkAbsenceByUniqStr(
        userRepository::existsByEmail, userEntityFromSave.getEmail());

    // Преобразование дат рождения пользователя и рождения детей
    userEntityFromSave.setDateBirth(dateTimeService.strToDate(userSave.getDateBirth()));
    List<LocalDate> childBirthList = dateTimeService.strToDate(userSave.getBirthDateChildren());

    // Проверка дат рождения пользователя и детей на предшествие текущей даты
    dateTimeService.checkDateBeforeNow(userEntityFromSave.getDateBirth());
    dateTimeService.checkDateBeforeNow(childBirthList);

    userEntityFromSave.setId(RandomValue.randomString(R.ID_LENGTH));
    usersRolesService.addUserRole(userEntityFromSave, RDB.ROLE_USER);

    userRepository.saveAndFlush(userEntityFromSave);
    log.info("DB. User with email \"{}\" created.", userSave.getEmail());
    childBirthList.forEach(childBirth -> usersChildrenService.addUserChild(userEntityFromSave, childBirth));
  }

  /**
   * Возвращает пользователя об учреждении по его ID
   * @param idUser ID пользователя
   * @return информация о пользователе
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  @Override
  public UserInfo read(String idUser) throws NotFoundException {

    // Получение пользователя по его ID, если пользователь существует
    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    UserEntity userEntityFromRequest = getUserEntity(prepareIdUser);

    return UserDBConverter.toInfo(userEntityFromRequest, usersRolesService.getRolesByUser(userEntityFromRequest));
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
    mailService.checkEmailElseThrow(userSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = UserDBConverter
        .fromSave(userSave, dbIntegrityService::preparePostgreSQLString);

    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);

    // Проверка существования города по ID
    dbIntegrityService.checkExistenceById(
        cityDBService.getRepository()::existsById, userEntityFromSave.getCityEntity());

    // Проверка отсутствия пользователя с отличным от данного ID и данным email
    dbIntegrityService.checkAbsenceAnotherByUniqStr(
        userRepository::existsByIdIsNotAndName, prepareIdUser, userEntityFromSave.getEmail());

    // Получение пользователя по его ID, если пользователь существует
    UserEntity userEntityFromDB = getUserEntity(prepareIdUser);

    // Преобразование дат рождения пользователя и рождения детей
    userEntityFromDB.setDateBirth(dateTimeService.strToDate(userSave.getDateBirth()));
    List<LocalDate> childBirthList = dateTimeService.strToDate(userSave.getBirthDateChildren());

    // Проверка дат рождения пользователя и детей на предшествие текущей даты
    dateTimeService.checkDateBeforeNow(userEntityFromDB.getDateBirth());
    dateTimeService.checkDateBeforeNow(childBirthList);

    userEntityFromDB.setEmail(userEntityFromSave.getEmail());
    userEntityFromDB.setName(userEntityFromSave.getName());

    userRepository.saveAndFlush(userEntityFromDB);
    log.info("DB. User with ID \"{}\" updated.", idUser);
    childBirthList.forEach(childBirth -> usersChildrenService.addUserChild(userEntityFromDB, childBirth));
  }

  /**
   * Удаляет пользователя по его ID или удаляет роль "ROLE_USER" у администратора
   * @param idUser ID пользователя
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  @Override
  public void delete(String idUser) throws NotFoundException {

    // Получение пользователя по его ID, если пользователь существует
    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    UserEntity userEntityFromRequest = getUserEntity(prepareIdUser);

    // Если есть роль "ROLE_ADMIN", удаление роли "ROLE_USER", иначе удаление пользователя и его токена восстановления
    if (usersRolesService.hasUserRole(prepareIdUser, RDB.ID_ROLE_ADMIN)) {
      usersRolesService.deleteUserRole(prepareIdUser, RDB.ID_ROLE_USER);
      userRepository.saveAndFlush(userEntityFromRequest);
      log.info("DB. User with ID \"{}\" updated. Removed role \"{}\"", idUser, RDB.NAME_ROLE_USER);
    } else {
      userRepository.deleteById(prepareIdUser);
      log.info("DB. User with ID \"{}\" deleted.", idUser);
      tokenCodeService.removeRefreshToken(prepareIdUser);
    }
  }

  /**
   * Возвращает дополнительные данные для пользователя.
   * Данные содержат в себе множества кратких информаций о городах
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
                          .collect(Collectors.toList()))
        .build();
  }

  /**
   * Возвращает модель пользователя по его ID
   * @param prepareId подготовленное для бд ID пользователя
   * @return модель пользователя
   * @throws NotFoundException если пользователь не найден
   */
  private UserEntity getUserEntity(String prepareId) throws NotFoundException {

    return userRepository.findById(prepareId)
        .orElseThrow(() -> new NotFoundException(String.format(
            "User with ID \"%s\" not found", prepareId)));
  }

  /**
   * Проверяет существование пользователя по email
   * @param email почта пользователя
   * @return {@code true}, если пользователь существует
   */
  @Override
  public boolean existsByEmail(String email) {

    String prepareEmail = dbIntegrityService.preparePostgreSQLString(email);
    return userRepository.existsByEmail(prepareEmail);
  }
}
