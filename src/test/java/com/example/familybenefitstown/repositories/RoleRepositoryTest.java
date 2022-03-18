package com.example.familybenefitstown.repositories;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.res_part_rest_api.dto.entities.RoleEntity;
import com.example.familybenefitstown.res_part_rest_api.dto.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FamilyBenefitsTownApplication.class})
public class RoleRepositoryTest {

  @Autowired
  private RoleRepository roleRepository;

  private static final String ID_TEST_ROLE = "id_test_role";
  private static final String NAME_TEST_ROLE = "testRole";

  /**
   * Создает тестовую роль перед каждым тестом
   */
  private void createRoleEntity_TestRole() {

    log.info("Start createRoleEntity_TestRole");

    log.info("Save test role");
    roleRepository.save(RoleEntity
                            .builder()
                            .id(ID_TEST_ROLE)
                            .name(NAME_TEST_ROLE)
                            .build());

    log.info("End createRoleEntity_TestRole");
  }

  /**
   * Удаляет тестовую роль после каждого теста
   */
  private void deleteRoleEntity_TestRole() {

    log.info("Start deleteRoleEntity_TestRole");

    log.info("Exists test role");
    if (roleRepository.existsById(ID_TEST_ROLE)) {
      log.info("Delete test role");
      roleRepository.deleteById(ID_TEST_ROLE);
    }

    log.info("End deleteRoleEntity_TestRole");
  }

  /**
   * <p>
   *   Тестирует таблицу роли <b>"role"</b> по модели {@link RoleEntity}, без связанных таблиц, сущностей.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Получение тестовой записи и всех её полей.</li>
   * </ol>
   */
  @Test
  public void baseTableTest_role() {

    log.info("Start TEST baseTableTest_role");

    createRoleEntity_TestRole();

    log.info("Get test role");
    RoleEntity testRole = roleRepository.findById(ID_TEST_ROLE).orElseThrow();

    AssertionsForClassTypes.assertThat(testRole.getId()).isEqualTo(ID_TEST_ROLE);
    AssertionsForClassTypes.assertThat(testRole.getName()).isEqualTo(NAME_TEST_ROLE);

    testRole.setName("new_name");
    log.info("Save changed test role");
    roleRepository.save(testRole);

    log.info("Get test role");
    testRole = roleRepository.findById(ID_TEST_ROLE).orElseThrow();

    AssertionsForClassTypes.assertThat(testRole.getId()).isEqualTo(ID_TEST_ROLE);
    AssertionsForClassTypes.assertThat(testRole.getName()).isEqualTo("new_name");

    deleteRoleEntity_TestRole();

    log.info("End TEST baseTableTest_role");
  }
}
