package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.api_models.admin.AdminSave;
import com.example.familybenefitstown.converters.AdminDBConverter;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.resources.RDB;
import com.example.familybenefitstown.security.generator.RandomValue;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.services.interfaces.MailService;
import com.example.familybenefitstown.services.interfaces.SuperAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса, управляющего объектом "супер-администратор"
 */
@Slf4j
@Service
public class SuperAdminServiceFB implements SuperAdminService {

  /**
   * Репозиторий, работающий с моделью таблицы "user"
   */
  private final UserRepository userRepository;

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
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param mailService интерфейс сервиса для отправки сообщений на электронную почту
   */
  @Autowired
  public SuperAdminServiceFB(UserRepository userRepository,
                             DBIntegrityService dbIntegrityService,
                             MailService mailService) {
    this.userRepository = userRepository;
    this.dbIntegrityService = dbIntegrityService;
    this.mailService = mailService;
  }

  /**
   * Создает администратора по запросу на сохранение
   * @param adminSave объект запроса на сохранение администратора
   * @throws AlreadyExistsException если администратор или пользователь с указанным email уже существует
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  @Override
  @Transactional
  public void create(AdminSave adminSave) throws AlreadyExistsException, InvalidEmailException, InvalidStringException {

    // Проверка строки email на соответствие формату email
    mailService.checkEmailElseThrow(adminSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = AdminDBConverter
        .fromSave(adminSave, dbIntegrityService::preparePostgreSQLString);

    // Проверка на существование пользователя или администратора по email
    dbIntegrityService.checkAbsenceByUniqStr(
        userRepository::existsByEmail, userEntityFromSave.getEmail());

    userEntityFromSave.setId(RandomValue.randomString(R.ID_LENGTH));

    userRepository.save(userEntityFromSave);
    userRepository.addRoleToUser(userEntityFromSave.getId(), RDB.ID_ROLE_ADMIN);
    log.info("DB. Administrator with email \"{}\" created.", adminSave.getEmail());
  }

  /**
   * Удаляет администратора по его ID или удаляет роль "ROLE_ADMIN" у пользователя
   * @param idAdmin ID администратора
   * @throws NotFoundException если администратор с указанным ID не найден
   */
  @Override
  @Transactional
  public void delete(String idAdmin) throws NotFoundException {

    // Проверка на существование пользователя или администратора по ID
    String preparedIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);
    dbIntegrityService.checkExistenceById(userRepository::existsById, preparedIdAdmin);

    // Проверка наличия роли "ROLE_ADMIN" у пользователя
    checkHasRoleElseThrowNotFound(preparedIdAdmin, RDB.ID_ROLE_ADMIN);

    // Если есть роль "ROLE_USER", удаление роли "ROLE_ADMIN", иначе удаление пользователя и его токена восстановления с кодом входа
    if (userRepository.hasUserRole(preparedIdAdmin, RDB.ID_ROLE_ADMIN)) {
      userRepository.deleteRoleFromUser(preparedIdAdmin, RDB.ID_ROLE_ADMIN);
      log.info("DB. Administrator with ID \"{}\" updated. Removed role \"{}\".", idAdmin, RDB.NAME_ROLE_ADMIN);
    } else {
      userRepository.deleteById(preparedIdAdmin);
      log.info("DB. Administrator with ID \"{}\" deleted.", idAdmin);
    }
  }

  /**
   * Добавляет роль "ROLE_ADMIN" пользователю
   * @param idUser ID пользователя
   * @throws NotFoundException если пользователь с данным ID и ролью "ROLE_USER" не найден
   * @throws AlreadyExistsException если пользователь имеет роль "ROLE_ADMIN"
   */
  @Override
  @Transactional
  public void fromUser(String idUser) throws NotFoundException, AlreadyExistsException {

    // Проверка существования пользователя по ID
    String preparedIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    dbIntegrityService.checkExistenceById(userRepository::existsById, preparedIdUser);

    // Проверка наличия роли "ROLE_USER" у пользователя
    checkHasRoleElseThrowNotFound(preparedIdUser, RDB.ID_ROLE_USER);
    // Проверка отсутствия роли "ROLE_ADMIN" у пользователя
    checkNotHasRoleElseThrowUserRole(preparedIdUser, RDB.ID_ROLE_ADMIN);

    userRepository.addRoleToUser(preparedIdUser, RDB.ID_ROLE_ADMIN);
    log.info("DB. User with ID \"{}\" updated. Added role \"{}\"", idUser, RDB.NAME_ROLE_ADMIN);
  }

  /**
   * Добавляет роль "ROLE_USER" администратору
   * @param idAdmin ID администратора
   * @throws NotFoundException если пользователь с данным ID и ролью "ROLE_ADMIN" не найден
   * @throws AlreadyExistsException если пользователь имеет роль "ROLE_USER"
   */
  @Override
  @Transactional
  public void toUser(String idAdmin) throws NotFoundException, AlreadyExistsException {

    // Проверка существования пользователя по ID
    String preparedIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);
    dbIntegrityService.checkExistenceById(userRepository::existsById, preparedIdAdmin);

    // Проверка наличия роли "ROLE_ADMIN" у пользователя
    checkHasRoleElseThrowNotFound(preparedIdAdmin, RDB.ID_ROLE_ADMIN);
    // Проверка отсутствия роли "ROLE_USER" у пользователя
    checkNotHasRoleElseThrowUserRole(preparedIdAdmin, RDB.ID_ROLE_USER);

    userRepository.addRoleToUser(preparedIdAdmin, RDB.ID_ROLE_USER);
    log.info("DB. Administrator with ID \"{}\" updated. Added role \"{}\"", idAdmin, RDB.NAME_ROLE_USER);
  }

  /**
   * Передает роль "ROLE_SUPER_ADMIN" указанному администратору, удаляя данную роль у текущего администратора
   * @param idAdmin ID администратора
   * @throws NotFoundException если администратор с данным ID не найден
   */
  @Override
  @Transactional
  public void toSuper(String idAdmin) throws NotFoundException {

    // Проверка существования пользователя по ID
    String preparedIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);
    dbIntegrityService.checkExistenceById(userRepository::existsById, preparedIdAdmin);

    // Проверка наличия роли "ROLE_ADMIN" у пользователя
    checkHasRoleElseThrowNotFound(preparedIdAdmin, RDB.ID_ROLE_ADMIN);

    // Передача роли "ROLE_SUPER_ADMIN"
    UserEntity userEntitySuperAdmin = userRepository.getSuperAdmin();
    userRepository.deleteRoleFromUser(userEntitySuperAdmin.getId(), RDB.ID_ROLE_SUPER_ADMIN);
    userRepository.addRoleToUser(preparedIdAdmin, RDB.ID_ROLE_SUPER_ADMIN);

    log.info("DB. Administrator with ID \"{}\" updated. Added role \"{}\"", idAdmin, RDB.ROLE_SUPER_ADMIN);
  }

  /**
   * Проверяет наличие у пользователя роли. В случае отсутствия, выбрасывается {@link NotFoundException}
   * @param idUser ID проверяемого пользователя
   * @param idRole ID проверяемой роли
   * @throws NotFoundException если пользователь с данной ролью не найден
   */
  private void checkHasRoleElseThrowNotFound(String idUser, String idRole) throws NotFoundException {

    if (!userRepository.hasUserRole(idUser, idRole)) {
      throw new NotFoundException(String.format(
          "User with ID \"%s\" doesn't have role with ID \"%s\"", idUser, idRole));
    }
  }

  /**
   * Проверяет отсутствие у пользователя роли. В случае наличия, выбрасывается {@link AlreadyExistsException}
   * @param idUser ID проверяемого пользователя
   * @param idRole ID проверяемой роли
   * @throws AlreadyExistsException если пользователь имеет роль
   */
  private void checkNotHasRoleElseThrowUserRole(String idUser, String idRole) throws AlreadyExistsException {

    if (userRepository.hasUserRole(idUser, idRole)) {
      throw new AlreadyExistsException(String.format(
          "User with ID \"%s\" already has role with ID \"%s\"", idUser, idRole));
    }
  }
}
