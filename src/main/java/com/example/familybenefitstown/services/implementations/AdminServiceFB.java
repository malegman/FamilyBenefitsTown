package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.api_models.admin.AdminInfo;
import com.example.familybenefitstown.api_models.admin.AdminSave;
import com.example.familybenefitstown.converters.AdminDBConverter;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.dto.repositories.strong.UserRepository;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.services.interfaces.AdminService;
import com.example.familybenefitstown.services.interfaces.MailService;
import com.example.familybenefitstown.services.interfaces.UsersRolesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса, управляющего объектом "администратор"
 */
@Slf4j
@Service
public class AdminServiceFB implements AdminService {

  /**
   * Репозиторий, работающий с моделью таблицы "user"
   */
  private final UserRepository userRepository;

  /**
   * Интерфейс сервиса, управляющего связью пользователей и ролей
   */
  private final UsersRolesService usersRolesService;

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
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param mailService интерфейс сервиса для отправки сообщений на электронную почту
   */
  @Autowired
  public AdminServiceFB(UserRepository userRepository,
                        UsersRolesService usersRolesService, DBIntegrityService dbIntegrityService,
                        MailService mailService) {
    this.userRepository = userRepository;
    this.usersRolesService = usersRolesService;
    this.dbIntegrityService = dbIntegrityService;
    this.mailService = mailService;
  }

  /**
   * Возвращает администратора об учреждении по его ID
   * @param idAdmin ID администратора
   * @return информация об администраторе
   * @throws NotFoundException если администратор с данным ID не найдено
   */
  @Override
  public AdminInfo read(String idAdmin) throws NotFoundException {

    String prepareIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);

    // Получение администратора по его ID, если администратора существует
    UserEntity userEntityFromRequest = userRepository.findById(prepareIdAdmin)
        .orElseThrow(() -> new NotFoundException(String.format(
            "Administrator with ID \"%s\" not found", idAdmin)));

    return AdminDBConverter.toInfo(userEntityFromRequest, usersRolesService.getRolesByUser(userEntityFromRequest));
  }

  /**
   * Обновляет администратора по запросу на сохранение
   * @param idAdmin ID администратора
   * @param adminSave объект запроса на сохранение администратора
   * @throws NotFoundException если администратор с указанными данными не найден
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws AlreadyExistsException если администратор или пользователь с отличным ID и данным email уже существует
   */
  @Override
  public void update(String idAdmin, AdminSave adminSave) throws NotFoundException, InvalidEmailException, AlreadyExistsException {

    // Проверка строки email на соответствие формату email
    mailService.checkEmailElseThrow(adminSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = AdminDBConverter
        .fromSave(adminSave, dbIntegrityService::preparePostgreSQLString);

    String prepareIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);

    // Проверка отсутствия пользователя с отличным от данного ID и данным email
    dbIntegrityService.checkAbsenceAnotherByUniqStr(
        userRepository::existsByIdIsNotAndName, prepareIdAdmin, userEntityFromSave.getEmail());

    // Получение администратора по его ID, если администратора существует
    UserEntity userEntityFromDB = userRepository.findById(prepareIdAdmin)
        .orElseThrow(() -> new NotFoundException(String.format(
            "Administrator with ID \"%s\" not found", userEntityFromSave.getId())));

    userEntityFromDB.setEmail(userEntityFromSave.getEmail());
    userEntityFromDB.setName(userEntityFromSave.getName());

    userRepository.saveAndFlush(userEntityFromDB);
    log.info("DB. Administrator with ID \"{}\" updated.", idAdmin);
  }
}

