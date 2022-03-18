package com.example.familybenefitstown.services;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.admin.AdminSave;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.resources.RDB;
import com.example.familybenefitstown.resources.TE;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FamilyBenefitsTownApplication.class})
public class AdminServiceTest {

  @Autowired
  private AdminService adminService;

  @Autowired
  private UserRepository userRepository;

  /**
   * Создает тестовых пользователей перед каждым тестом
   */
  @Transactional
  public void createTestUsers() {

    log.info("Start createTestUsers");

    log.info("Save test Admin");
    userRepository.save(TE.UE_ADMIN);
    log.info("Add role ADMIN to test Admin");
    userRepository.addRoleToUser(TE.UE_ADMIN.getId(), RDB.ID_ROLE_ADMIN);

    log.info("Save test User");
    userRepository.save(TE.UE_USER);
    log.info("Add role USER to test User");
    userRepository.addRoleToUser(TE.UE_USER.getId(), RDB.ID_ROLE_USER);

    log.info("Save test UserAdmin");
    userRepository.save(TE.UE_USER_ADMIN);
    log.info("Add role USER to test UserAdmin");
    userRepository.addRoleToUser(TE.UE_USER_ADMIN.getId(), RDB.ID_ROLE_USER);
    log.info("Add role ADMIN to test UserAdmin");
    userRepository.addRoleToUser(TE.UE_USER_ADMIN.getId(), RDB.ID_ROLE_ADMIN);

    log.info("End createTestUsers");
  }

  /**
   * Удаляет тестовых пользователей после каждого теста
   */
  private void deleteTestUsers() {

    log.info("Start deleteTestUsers");

    log.info("Exists test Admin");
    if (userRepository.existsById(TE.UE_ADMIN.getId())) {
      log.info("Delete test Admin");
      userRepository.deleteById(TE.UE_ADMIN.getId());
    }
    log.info("Exists test User");
    if (userRepository.existsById(TE.UE_USER.getId())) {
      log.info("Delete test User");
      userRepository.deleteById(TE.UE_USER.getId());
    }
    log.info("Exists test UserAdmin");
    if (userRepository.existsById(TE.UE_USER_ADMIN.getId())) {
      log.info("Delete test UserAdmin");
      userRepository.deleteById(TE.UE_USER_ADMIN.getId());
    }

    log.info("End deleteTestUsers");
  }

  /**
   * <p>
   *   Тест метода {@code read} сервиса {@link AdminService}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Запрос несуществующего администратора, строкой с SQL-инъекцией.</li>
   *   <li>Запрос существующего пользователя без роли администратора.</li>
   *   <li>Запрос существующего администратора с ролью пользователя.</li>
   *   <li>Запрос существующего администратора без роли пользователя.</li>
   * </ol>
   */
  @Test
  @Transactional
  public void test_read() {

    log.info("Start test_read");

    createTestUsers();

    // 1. Запрос несуществующего администратора, строкой с SQL-инъекцией.

    AssertionsForClassTypes.assertThatExceptionOfType(NotFoundException.class)
        .isThrownBy(() -> adminService.read("';SELECT * FROM family_benefits_town.user;--"));

    // 2. Запрос существующего пользователя без роли администратора.

    AssertionsForClassTypes.assertThatNoException()
        .isThrownBy(() -> adminService.read(TE.UE_USER.getId()));

    // 3. Запрос существующего администратора с ролью пользователя.

    try {
      AdminInfo adminInfo = adminService.read(TE.UE_USER_ADMIN.getId());
      AssertionsForClassTypes.assertThat(adminInfo.getId()).isEqualTo(TE.UE_USER_ADMIN.getId());
      AssertionsForClassTypes.assertThat(adminInfo.getName()).isEqualTo(TE.UE_USER_ADMIN.getName());
      AssertionsForClassTypes.assertThat(adminInfo.getEmail()).isEqualTo(TE.UE_USER_ADMIN.getEmail());
      AssertionsForClassTypes.assertThat(adminInfo.getNameRoleList())
          .isEqualTo(List.of(RDB.ROLE_USER, RDB.ROLE_ADMIN));
    } catch (NotFoundException e) {
      log.info(e.getMessage());
    }

    // 4. Запрос существующего администратора без роли пользователя.

    try {
      AdminInfo adminInfo = adminService.read(TE.UE_USER_ADMIN.getId());
      AssertionsForClassTypes.assertThat(adminInfo.getId()).isEqualTo(TE.UE_USER_ADMIN.getId());
      AssertionsForClassTypes.assertThat(adminInfo.getName()).isEqualTo(TE.UE_USER_ADMIN.getName());
      AssertionsForClassTypes.assertThat(adminInfo.getEmail()).isEqualTo(TE.UE_USER_ADMIN.getEmail());
      AssertionsForClassTypes.assertThat(adminInfo.getNameRoleList())
          .isEqualTo(List.of(RDB.ROLE_ADMIN));
    } catch (NotFoundException e) {
      log.info(e.getMessage());
    }

    deleteTestUsers();

    log.info("End test_read");
  }

  /**
   * <p>
   *   Тест метода {@code update} сервиса {@link AdminService}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Обновление несуществующего администратора.</li>
   *   <li>Обновление существующего администратора с некорректными полями "name" и "email".</li>
   *   <li>Обновление существующего администратора с повторяющимся email.</li>
   *   <li>Обновление существующего администратора корректными данными.</li>
   * </ol>
   */
  @Test
  @Transactional
  public void test_update() {

    log.info("Start test_update");

    createTestUsers();

    AdminSave correctAdminSave = new AdminSave("new name admin", "new.email.admin@email.com");
    AdminSave nullNameAdminSave = new AdminSave(null, "new.email.admin@email.com");
    AdminSave spaceNameAdminSave = new AdminSave("   ", "new.email.admin@email.com");
    AdminSave nullEmailAdminSave = new AdminSave("new name admin", null);
    AdminSave notEmailAdminSave = new AdminSave("new name admin", "new.email.adminEmail.com");
    AdminSave repEmailAdminSave = new AdminSave("new name admin", TE.UE_USER.getEmail());

    // 1. Обновление несуществующего администратора.

    AssertionsForClassTypes.assertThatExceptionOfType(NotFoundException.class)
        .isThrownBy(() -> adminService.update("not_id", correctAdminSave));

    // 2. Обновление существующего администратора с некорректными полями "name" и "email".

    AssertionsForClassTypes.assertThatExceptionOfType(InvalidStringException.class)
        .isThrownBy(() -> adminService.update(TE.UE_USER_ADMIN.getId(), nullNameAdminSave));
    AssertionsForClassTypes.assertThatExceptionOfType(InvalidStringException.class)
        .isThrownBy(() -> adminService.update(TE.UE_USER_ADMIN.getId(), spaceNameAdminSave));
    AssertionsForClassTypes.assertThatExceptionOfType(InvalidEmailException.class)
        .isThrownBy(() -> adminService.update(TE.UE_USER_ADMIN.getId(), nullEmailAdminSave));
    AssertionsForClassTypes.assertThatExceptionOfType(InvalidEmailException.class)
        .isThrownBy(() -> adminService.update(TE.UE_USER_ADMIN.getId(), notEmailAdminSave));

    // 3. Обновление существующего администратора с повторяющимся email.

    AssertionsForClassTypes.assertThatExceptionOfType(AlreadyExistsException.class)
        .isThrownBy(() -> adminService.update(TE.UE_USER_ADMIN.getId(), repEmailAdminSave));

    // 4. Обновление существующего администратора корректными данными.

    try {
      adminService.update(TE.UE_USER_ADMIN.getId(), correctAdminSave);
    } catch (Exception e) {
      log.info(e.getMessage());
    }

    AssertionsForClassTypes.assertThat(
        userRepository.findById(TE.UE_USER_ADMIN.getId()).orElseThrow().getName())
        .isEqualTo(correctAdminSave.getName());

    deleteTestUsers();

    log.info("End test_update");
  }
}
