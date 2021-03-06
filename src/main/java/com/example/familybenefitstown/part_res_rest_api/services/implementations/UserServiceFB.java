package com.example.familybenefitstown.part_res_rest_api.services.implementations;

import com.example.familybenefitstown.exceptions.*;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserInitData;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserSave;
import com.example.familybenefitstown.part_res_rest_api.converters.CityDBConverter;
import com.example.familybenefitstown.part_res_rest_api.converters.UserDBConverter;
import com.example.familybenefitstown.dto.entities.ChildBirthEntity;
import com.example.familybenefitstown.dto.entities.CityEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.dto.repositories.ChildBirthRepository;
import com.example.familybenefitstown.dto.repositories.CityRepository;
import com.example.familybenefitstown.dto.repositories.RoleRepository;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.UserService;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.resources.RDB;
import com.example.familybenefitstown.security.RandomValue;
import com.example.familybenefitstown.security.DBSecuritySupport;
import com.example.familybenefitstown.security.DateTimeSupport;
import com.example.familybenefitstown.security.MailSecuritySupport;
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
  private final ChildBirthRepository childBirthRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "role"
   */
  private final RoleRepository roleRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "city"
   */
  private final CityRepository cityRepository;

  /**
   * Конструктор для инициализации интерфейсов репозиториев и сервисов
   * @param userRepository репозиторий, работающий с моделью таблицы "user"
   * @param childBirthRepository репозиторий, работающий с моделью таблицы "child"
   * @param roleRepository репозиторий, работающий с моделью таблицы "role"
   * @param cityRepository репозиторий, работающий с моделью таблицы "city"
   */
  @Autowired
  public UserServiceFB(UserRepository userRepository,
                       ChildBirthRepository childBirthRepository,
                       RoleRepository roleRepository,
                       CityRepository cityRepository) {
    this.userRepository = userRepository;
    this.childBirthRepository = childBirthRepository;
    this.roleRepository = roleRepository;
    this.cityRepository = cityRepository;
  }

  /**
   * Создает пользователя по запросу на сохранение. Регистрация гостя
   * @param userSave объект запроса на сохранение пользователя
   * @throws NotFoundException если город или критерии с указанными данными не найдены
   * @throws AlreadyExistsException если администратор или пользователь с указанным email уже существует
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws DateFormatException если даты рождения пользователя или детей не соответствуют формату "dd.mm.yyyy"
   * @throws DateTimeException если даты рождения пользователя или детей позже текущей даты
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  @Override
  @Transactional
  public void create(UserSave userSave) throws
      NotFoundException,
      AlreadyExistsException,
      InvalidEmailException,
      DateFormatException,
      DateTimeException,
      InvalidStringException {

    // Проверка строки email на соответствие формату email
    MailSecuritySupport.checkEmailElseThrow(userSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = UserDBConverter
        .fromSave(null, userSave, DBSecuritySupport::preparePostgreSQLString);

    // Проверка существования города по ID
    DBSecuritySupport.checkExistenceById(
        cityRepository::existsById, userEntityFromSave.getIdCity());

    // Проверка на отсутствие пользователя или администратора по email
    DBSecuritySupport.checkAbsenceByUniqStr(
        userRepository::existsByEmail, userEntityFromSave.getEmail());

    // Преобразование дат рождения пользователя и рождения детей
    userEntityFromSave.setDateBirth(DateTimeSupport.strToDate(userSave.getDateBirth()));
    List<LocalDate> childBirthList = DateTimeSupport.strToDate(userSave.getBirthDateChildren());

    // Проверка дат рождения пользователя и детей на предшествие текущей даты
    DateTimeSupport.checkDateBeforeNow(userEntityFromSave.getDateBirth());
    DateTimeSupport.checkDateBeforeNow(childBirthList);

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
    String preparedIdUser = DBSecuritySupport.preparePostgreSQLString(idUser);
    UserEntity userEntityFromRequest = getUserEntity(preparedIdUser);

    return UserDBConverter.toInfo(userEntityFromRequest,
                                  childBirthRepository.findAllByIdUser(preparedIdUser),
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
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  @Override
  @Transactional
  public void update(String idUser, UserSave userSave) throws
      NotFoundException,
      InvalidEmailException,
      DateFormatException,
      DateTimeException,
      AlreadyExistsException,
      InvalidStringException {

    // Проверка строки email на соответствие формату email
    MailSecuritySupport.checkEmailElseThrow(userSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = UserDBConverter
        .fromSave(idUser, userSave, DBSecuritySupport::preparePostgreSQLString);

    String preparedIdUser = userEntityFromSave.getId();

    // Проверка существования города по ID
    DBSecuritySupport.checkExistenceById(
        cityRepository::existsById, userEntityFromSave.getIdCity());

    // Проверка отсутствия пользователя с отличным от данного ID и данным email
    DBSecuritySupport.checkAbsenceAnotherByUniqStr(
        userRepository::existsByIdIsNotAndEmail, preparedIdUser, userEntityFromSave.getEmail());

    // Получение пользователя по его ID, если пользователь существует
    UserEntity userEntityFromDB = getUserEntity(preparedIdUser);

    // Преобразование дат рождения пользователя и рождения детей
    userEntityFromDB.setDateBirth(DateTimeSupport.strToDate(userSave.getDateBirth()));
    List<LocalDate> childBirthList = DateTimeSupport.strToDate(userSave.getBirthDateChildren());

    // Проверка дат рождения пользователя и детей на предшествие текущей даты
    DateTimeSupport.checkDateBeforeNow(userEntityFromDB.getDateBirth());
    DateTimeSupport.checkDateBeforeNow(childBirthList);

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
    String preparedIdUser = DBSecuritySupport.preparePostgreSQLString(idUser);
    DBSecuritySupport.checkExistenceById(userRepository::existsById, preparedIdUser);

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

      // Получение существующей модели рождения ребенка или же создание новой
      Optional<ChildBirthEntity> childEntityOpt = childBirthRepository.findByDateBirth(childBirth);

      if (childEntityOpt.isPresent()) {
        userRepository.addChildToUser(idUser, childEntityOpt.get().getId());

      } else {
        ChildBirthEntity newChildBirthEntity = new ChildBirthEntity(RandomValue.randomString(R.ID_LENGTH), childBirth);
        childBirthRepository.save(newChildBirthEntity);
        userRepository.addChildToUser(idUser, newChildBirthEntity.getId());
      }
    }
  }
}
