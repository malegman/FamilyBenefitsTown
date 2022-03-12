package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.api_models.admin.AdminSave;
import com.example.familybenefitstown.converters.AdminDBConverter;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.resources.RDB;
import com.example.familybenefitstown.security.generator.RandomValue;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.security.services.interfaces.TokenCodeService;
import com.example.familybenefitstown.services.interfaces.MailService;
import com.example.familybenefitstown.services.interfaces.SuperAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
   * @param tokenCodeService интерфейс сервиса для работы с токенами доступа (в формате jwt) и восстановления и кодом для входа
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param mailService интерфейс сервиса для отправки сообщений на электронную почту
   */
  @Autowired
  public SuperAdminServiceFB(UserRepository userRepository,
                             TokenCodeService tokenCodeService,
                             DBIntegrityService dbIntegrityService,
                             MailService mailService) {
    this.userRepository = userRepository;
    this.tokenCodeService = tokenCodeService;
    this.dbIntegrityService = dbIntegrityService;
    this.mailService = mailService;
  }

  /**
   * Создает администратора по запросу на сохранение
   * @param adminSave объект запроса на сохранение администратора
   * @throws AlreadyExistsException если администратор или пользователь с указанным email уже существует
   * @throws InvalidEmailException если указанный "email" не является email
   */
  @Override
  public void create(AdminSave adminSave) throws AlreadyExistsException, InvalidEmailException {

    // Проверка строки email на соответствие формату email
    mailService.checkEmailElseThrow(adminSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = AdminDBConverter
        .fromSave(adminSave, dbIntegrityService::preparePostgreSQLString);

    // Проверка на существование пользователя или администратора по email
    dbIntegrityService.checkAbsenceByUniqStr(
        userRepository::existsByEmail, userEntityFromSave.getEmail());

    userEntityFromSave.setId(RandomValue.randomString(R.ID_LENGTH));
    userEntityFromSave.addRole(RDB.ROLE_ADMIN);

    userRepository.save(userEntityFromSave);
    log.info("DB. Administrator with email \"{}\" created.", adminSave.getEmail());
  }

  /**
   * Удаляет администратора по его ID или удаляет роль "ROLE_ADMIN" у пользователя
   * @param idAdmin ID администратора
   * @throws NotFoundException если администратор с указанным ID не найден
   */
  @Override
  public void delete(String idAdmin) throws NotFoundException {

    // Получение пользователя по его ID, если пользователь существует
    String prepareIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);
    UserEntity userEntityFromRequest = getUserEntity(prepareIdAdmin, "Administrator");

    // Проверка наличия роли "ROLE_ADMIN" у пользователя
    checkHasRoleElseThrowNotFound(userEntityFromRequest, RDB.ROLE_ADMIN);

    // Если есть роль "ROLE_USER", удаление роли "ROLE_ADMIN", иначе удаление пользователя и его токена восстановления с кодом входа
    if (userEntityFromRequest.hasRole(RDB.ROLE_USER)) {
      userEntityFromRequest.deleteRole(RDB.ROLE_ADMIN);
      userRepository.save(userEntityFromRequest);
      log.info("DB. Administrator with ID \"{}\" updated. Removed role \"{}\".", idAdmin, RDB.NAME_ROLE_ADMIN);
    } else {
      userRepository.deleteById(prepareIdAdmin);
      log.info("DB. Administrator with ID \"{}\" deleted.", idAdmin);
      tokenCodeService.removeTokenCodeByIdUser(prepareIdAdmin);
    }
  }

  /**
   * Добавляет роль "ROLE_ADMIN" пользователю
   * @param idUser ID пользователя
   * @throws NotFoundException если пользователь с данным ID и ролью "ROLE_USER" не найден
   * @throws AlreadyExistsException если пользователь имеет роль "ROLE_ADMIN"
   */
  @Override
  public void fromUser(String idUser) throws NotFoundException, AlreadyExistsException {

    // Получение пользователя по его ID, если пользователь существует
    String prepareIdUser = dbIntegrityService.preparePostgreSQLString(idUser);
    UserEntity userEntityFromRequest = getUserEntity(prepareIdUser, "User");

    // Проверка наличия роли "ROLE_USER" у пользователя
    checkHasRoleElseThrowNotFound(userEntityFromRequest, RDB.ROLE_USER);
    // Проверка отсутствия роли "ROLE_ADMIN" у пользователя
    checkNotHasRoleElseThrowUserRole(userEntityFromRequest, RDB.ROLE_ADMIN);

    userEntityFromRequest.addRole(RDB.ROLE_ADMIN);

    userRepository.save(userEntityFromRequest);
    log.info("DB. User with ID \"{}\" updated. Added role \"{}\"", idUser, RDB.NAME_ROLE_ADMIN);
  }

  /**
   * Добавляет роль "ROLE_USER" администратору
   * @param idAdmin ID администратора
   * @throws NotFoundException если пользователь с данным ID и ролью "ROLE_ADMIN" не найден
   * @throws AlreadyExistsException если пользователь имеет роль "ROLE_USER"
   */
  @Override
  public void toUser(String idAdmin) throws NotFoundException, AlreadyExistsException {

    // Получение пользователя по его ID, если пользователь существует
    String prepareIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);
    UserEntity userEntityFromRequest = getUserEntity(prepareIdAdmin, "Administrator");

    // Проверка наличия роли "ROLE_ADMIN" у пользователя
    checkHasRoleElseThrowNotFound(userEntityFromRequest, RDB.ROLE_ADMIN);
    // Проверка отсутствия роли "ROLE_USER" у пользователя
    checkNotHasRoleElseThrowUserRole(userEntityFromRequest, RDB.ROLE_USER);

    userEntityFromRequest.addRole(RDB.ROLE_USER);

    userRepository.save(userEntityFromRequest);
    log.info("DB. Administrator with ID \"{}\" updated. Added role \"{}\"", idAdmin, RDB.NAME_ROLE_USER);
  }

  /**
   * Передает роль "ROLE_SUPER_ADMIN" указанному администратору, удаляя данную роль у текущего администратора
   * @param idAdmin ID администратора
   * @throws NotFoundException если администратор с данным ID не найден
   */
  @Override
  public void toSuper(String idAdmin) throws NotFoundException {

    // Получение пользователя по его ID, если пользователь существует
    String prepareIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);
    UserEntity userEntityFromRequest = getUserEntity(prepareIdAdmin, "Administrator");

    // Проверка наличия роли "ROLE_ADMIN" у пользователя
    checkHasRoleElseThrowNotFound(userEntityFromRequest, RDB.ROLE_ADMIN);

    // Передача роли "ROLE_SUPER_ADMIN"
    UserEntity userEntitySuperAdmin = userRepository.getSuperAdmin();
    userEntitySuperAdmin.deleteRole(RDB.ROLE_SUPER_ADMIN);
    userEntityFromRequest.addRole(RDB.ROLE_SUPER_ADMIN);

    userRepository.save(userEntitySuperAdmin);
    log.info("DB. Administrator with ID \"{}\" updated. Removed role \"{}\"", idAdmin, RDB.ROLE_SUPER_ADMIN);
    userRepository.save(userEntityFromRequest);
    log.info("DB. Administrator with ID \"{}\" updated. Added role \"{}\"", idAdmin, RDB.ROLE_SUPER_ADMIN);
  }

  /**
   * Возвращает модель пользователя по его ID
   * @param prepareId подготовленное для бд ID пользователя
   * @param nameObject название объекта, который удаляется: Администратор или пользователь
   * @return модель пользователя
   * @throws NotFoundException если пользователь не найден
   */
  private UserEntity getUserEntity(String prepareId, String nameObject) throws NotFoundException {

    return userRepository.findById(prepareId)
        .orElseThrow(() -> new NotFoundException(String.format(
            "%s with ID \"%s\" not found",nameObject, prepareId)));
  }

  /**
   * Проверяет наличие у пользователя роли. В случае отсутствия, выбрасывается {@link NotFoundException}
   * @param userEntity модель пользователя, у которого необходимо проверить наличие роли
   * @param roleEntity модель проверяемой роли
   * @throws NotFoundException если пользователь с данной ролью не найден
   */
  private void checkHasRoleElseThrowNotFound(UserEntity userEntity, RoleEntity roleEntity) throws NotFoundException {

    if (!userEntity.hasRole(roleEntity)) {
      throw new NotFoundException(String.format(
          "User with ID \"%s\" doesn't have role with ID \"%s\"", userEntity.getId(), roleEntity.getId()));
    }
  }

  /**
   * Проверяет отсутствие у пользователя роли. В случае наличия, выбрасывается {@link AlreadyExistsException}
   * @param userEntity модель пользователя, у которого необходимо проверить отсутствие роли
   * @param roleEntity модель проверяемой роли
   * @throws AlreadyExistsException если пользователь имеет роль
   */
  private void checkNotHasRoleElseThrowUserRole(UserEntity userEntity, RoleEntity roleEntity) throws AlreadyExistsException {

    if (userEntity.hasRole(roleEntity)) {
      throw new AlreadyExistsException(String.format(
          "User with ID \"%s\" already has role with ID \"%s\"", userEntity.getId(), roleEntity.getId()));
    }
  }
}
