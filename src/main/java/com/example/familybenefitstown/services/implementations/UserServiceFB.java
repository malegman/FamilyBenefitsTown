package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.api_models.user.UserInfo;
import com.example.familybenefitstown.api_models.user.UserInitData;
import com.example.familybenefitstown.api_models.user.UserSave;
import com.example.familybenefitstown.converters.CityDBConverter;
import com.example.familybenefitstown.converters.UserDBConverter;
import com.example.familybenefitstown.dto.entities.ChildEntity;
import com.example.familybenefitstown.dto.entities.CityEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.dto.repositories.ChildRepository;
import com.example.familybenefitstown.dto.repositories.CityRepository;
import com.example.familybenefitstown.dto.repositories.RoleRepository;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import com.example.familybenefitstown.exceptions.*;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.resources.RDB;
import com.example.familybenefitstown.security.generator.RandomValue;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.services.interfaces.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
   * Репозиторий, работающий с моделью таблицы "child"
   */
  private final ChildRepository childRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "role"
   */
  private final RoleRepository roleRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "city"
   */
  private final CityRepository cityRepository;

  /**
   * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  private final DateTimeService dateTimeService;

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
   * @param childRepository репозиторий, работающий с моделью таблицы "child"
   * @param roleRepository репозиторий, работающий с моделью таблицы "role"
   * @param cityRepository репозиторий, работающий с моделью таблицы "city"
   * @param dateTimeService интерфейс сервиса, который предоставляет методы для работы с датой и временем
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param mailService интерфейс сервиса для отправки сообщений на электронную почту
   */
  @Autowired
  public UserServiceFB(UserRepository userRepository,
                       ChildRepository childRepository,
                       RoleRepository roleRepository,
                       CityRepository cityRepository,
                       DateTimeService dateTimeService,
                       DBIntegrityService dbIntegrityService,
                       MailService mailService) {
    this.userRepository = userRepository;
    this.childRepository = childRepository;
    this.roleRepository = roleRepository;
    this.cityRepository = cityRepository;
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
  @Transactional
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
        cityRepository::existsById, userEntityFromSave.getIdCity());

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

    userRepository.save(userEntityFromSave);
    userRepository.addRoleToUser(userEntityFromSave.getId(), RDB.ID_ROLE_USER);
    userRepository.deleteAllChildrenFromUser(userEntityFromSave.getId());
    setChildrenToUser(userEntityFromSave.getId(), childBirthList);

    log.info("DB. User with email \"{}\" created.", userSave.getEmail());
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
    String preparedIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    UserEntity userEntityFromRequest = getUserEntity(preparedIdUser);

    return UserDBConverter.toInfo(userEntityFromRequest,
                                  childRepository.findAllByIdUser(preparedIdUser),
                                  roleRepository.findAllByIdUser(preparedIdUser),
                                  cityRepository.findByIdUser(preparedIdUser)
                                      .map(CityEntity::getName).orElse(null));
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
  @Transactional
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

    String preparedIdUser = dbIntegrityService.preparePostgreSQLString(idUser);

    // Проверка существования города по ID
    dbIntegrityService.checkExistenceById(
        cityRepository::existsById, userEntityFromSave.getIdCity());

    // Проверка отсутствия пользователя с отличным от данного ID и данным email
    dbIntegrityService.checkAbsenceAnotherByUniqStr(
        userRepository::existsByIdIsNotAndEmail, preparedIdUser, userEntityFromSave.getEmail());

    // Получение пользователя по его ID, если пользователь существует
    UserEntity userEntityFromDB = getUserEntity(preparedIdUser);

    // Преобразование дат рождения пользователя и рождения детей
    userEntityFromDB.setDateBirth(dateTimeService.strToDate(userSave.getDateBirth()));
    List<LocalDate> childBirthList = dateTimeService.strToDate(userSave.getBirthDateChildren());

    // Проверка дат рождения пользователя и детей на предшествие текущей даты
    dateTimeService.checkDateBeforeNow(userEntityFromDB.getDateBirth());
    dateTimeService.checkDateBeforeNow(childBirthList);

    userEntityFromDB.setEmail(userEntityFromSave.getEmail());
    userEntityFromDB.setName(userEntityFromSave.getName());

    userRepository.save(userEntityFromDB);
    userRepository.deleteAllChildrenFromUser(preparedIdUser);
    setChildrenToUser(preparedIdUser, childBirthList);
    log.info("DB. User with ID \"{}\" updated.", idUser);
  }

  /**
   * Удаляет пользователя по его ID или удаляет роль "ROLE_USER" у администратора
   * @param idUser ID пользователя
   * @throws NotFoundException если пользователь с указанным ID не найден
   */
  @Override
  @Transactional
  public void delete(String idUser) throws NotFoundException {

    // Проверка существования пользователя по ID
    String preparedIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    dbIntegrityService.checkExistenceById(userRepository::existsById, preparedIdUser);

    // Если есть роль "ROLE_ADMIN", удаление роли "ROLE_USER", иначе удаление пользователя и его токена восстановления
    if (userRepository.hasUserRole(preparedIdUser, RDB.ID_ROLE_ADMIN)) {
      userRepository.deleteRoleFromUser(preparedIdUser, RDB.ID_ROLE_ADMIN);
      log.info("DB. User with ID \"{}\" updated. Removed role \"{}\"", idUser, RDB.NAME_ROLE_USER);
    } else {
      userRepository.deleteById(preparedIdUser);
      log.info("DB. User with ID \"{}\" deleted.", idUser);
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
        .shortCitySet(cityRepository.findAll()
                          .stream()
                          .map(CityDBConverter::toShortInfo)
                          .collect(Collectors.toList()))
        .build();
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
   * Добавляет детей по их дням рождениям указанному пользователю по его ID
   * @param idUser ID пользователя, которому добавляются модели детей
   * @param childBirthList список дней рождений детей
   */
  private void setChildrenToUser(String idUser, List<LocalDate> childBirthList) {

    for (LocalDate childBirth : childBirthList) {

      // Получение существующей модели ребенка или же создание новой
      Optional<ChildEntity> childEntityOpt = childRepository.findByDateBirth(childBirth);

      if (childEntityOpt.isPresent()) {
        userRepository.addChildToUser(idUser, childEntityOpt.get().getId());

      } else {
        ChildEntity newChildEntity = new ChildEntity(RandomValue.randomString(R.ID_LENGTH), childBirth);
        childRepository.save(newChildEntity);
        userRepository.addChildToUser(idUser, newChildEntity.getId());
      }
    }
  }
}
