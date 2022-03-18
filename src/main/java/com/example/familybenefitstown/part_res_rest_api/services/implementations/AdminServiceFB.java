package com.example.familybenefitstown.part_res_rest_api.services.implementations;

import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminSave;
import com.example.familybenefitstown.part_res_rest_api.converters.AdminDBConverter;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.dto.repositories.RoleRepository;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.AdminService;
import com.example.familybenefitstown.security.services.interfaces.DBIntegrityService;
import com.example.familybenefitstown.part_auth.services.interfaces.MailService;
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
   * Репозиторий, работающий с моделью таблицы "role"
   */
  private final RoleRepository roleRepository;

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
   * @param roleRepository репозиторий, работающий с моделью таблицы "role"
   * @param dbIntegrityService интерфейс сервиса, отвечающего за целостность базы данных
   * @param mailService интерфейс сервиса для отправки сообщений на электронную почту
   */
  @Autowired
  public AdminServiceFB(UserRepository userRepository,
                        RoleRepository roleRepository,
                        DBIntegrityService dbIntegrityService,
                        MailService mailService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.dbIntegrityService = dbIntegrityService;
    this.mailService = mailService;
  }

  /**
   * Возвращает администратора об учреждении по его ID
   * @param idAdmin ID администратора
   * @return информация об администраторе
   * @throws NotFoundException если администратор с данным ID не найден
   */
  @Override
  public AdminInfo read(String idAdmin) throws NotFoundException {

    String preparedIdAdmin = dbIntegrityService.preparePostgreSQLString(idAdmin);

    // Получение администратора по его ID, если администратор существует
    UserEntity userEntityFromRequest = userRepository.findById(preparedIdAdmin).orElseThrow(
        () -> new NotFoundException(String.format("Administrator with ID \"%s\" not found", idAdmin)));

    return AdminDBConverter.toInfo(userEntityFromRequest, roleRepository.findAllByIdUser(preparedIdAdmin));
  }

  /**
   * Обновляет администратора по запросу на сохранение
   * @param idAdmin ID администратора
   * @param adminSave объект запроса на сохранение администратора
   * @throws NotFoundException если администратор с указанными данными не найден
   * @throws InvalidEmailException если указанный "email" не является email
   * @throws AlreadyExistsException если администратор или пользователь с отличным ID и данным email уже существует
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  @Override
  public void update(String idAdmin, AdminSave adminSave) throws NotFoundException, InvalidEmailException, AlreadyExistsException, InvalidStringException {

    // Проверка строки email на соответствие формату email
    mailService.checkEmailElseThrow(adminSave.getEmail());

    // Получение модели таблицы из запроса с подготовкой строковых значений для БД
    UserEntity userEntityFromSave = AdminDBConverter
        .fromSave(idAdmin, adminSave, dbIntegrityService::preparePostgreSQLString);
    String preparedIdAdmin = userEntityFromSave.getId();

    // Проверка отсутствия пользователя с отличным от данного ID и данным email
    dbIntegrityService.checkAbsenceAnotherByUniqStr(
        userRepository::existsByIdIsNotAndEmail, preparedIdAdmin, userEntityFromSave.getEmail());

    // Получение администратора по его ID, если администратора существует
    UserEntity userEntityFromDB = userRepository.findById(preparedIdAdmin).orElseThrow(
        () -> new NotFoundException(String.format("Administrator with ID \"%s\" not found", userEntityFromSave.getId())));

    userEntityFromDB.setEmail(userEntityFromSave.getEmail());
    userEntityFromDB.setName(userEntityFromSave.getName());

    userRepository.save(userEntityFromDB);
    log.info("DB. Administrator with ID \"{}\" updated.", idAdmin);
  }
}

