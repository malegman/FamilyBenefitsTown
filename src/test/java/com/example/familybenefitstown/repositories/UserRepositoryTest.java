package com.example.familybenefitstown.repositories;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.res_part_rest_api.dto.entities.*;
import com.example.familybenefitstown.res_part_rest_api.dto.repositories.*;
import com.example.familybenefitstown.resources.RDB;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FamilyBenefitsTownApplication.class})
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private ChildBirthRepository childBirthRepository;
  @Autowired
  private CityRepository cityRepository;
  @Autowired
  private RefreshTokenRepository refreshTokenRepository;
  @Autowired
  private LoginCodeRepository loginCodeRepository;

  private static final String ID_TEST_USER = "id_test_user";
  private static final String EMAIL_TEST_USER = "testUser@mail.com";
  private static final String NAME_TEST_USER = "testUser";
  private static final LocalDate DATE_BIRTH_TEST_USER = LocalDate.of(1990, 10, 10);

  /**
   * Создает тестового пользователя перед каждым тестом
   */
  private void createUserEntity_TestUser() {

    log.info("Start createUserEntity_TestUser");

    log.info("Save test user");
    userRepository.save(UserEntity.builder()
                            .id(ID_TEST_USER)
                            .email(EMAIL_TEST_USER)
                            .name(NAME_TEST_USER)
                            .dateBirth(DATE_BIRTH_TEST_USER)
                            .build());

    log.info("End createUserEntity_TestUser");
  }

  /**
   * Удаляет тестового пользователя после каждого теста
   */
  private void deleteUserEntity_TestUser() {

    log.info("Start deleteUserEntity_TestUser");

    log.info("Exists test user");
    if (userRepository.existsById(ID_TEST_USER)) {
      log.info("Delete test user");
      userRepository.deleteById(ID_TEST_USER);
    }

    log.info("End deleteUserEntity_TestUser");
  }

  /**
   * <p>
   *   Тестирует таблицу пользователя <b>"user"</b> по модели {@link UserEntity}, без связанных таблиц, сущностей.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Получение тестовой записи и всех её полей.</li>
   * </ol>
   */
  @Test
  public void baseTableTest_user() {

    log.info("Start TEST baseTableTest_user");

    createUserEntity_TestUser();

    log.info("Get test user");
    UserEntity testUser = userRepository.findById(ID_TEST_USER).orElseThrow();

    AssertionsForClassTypes.assertThat(testUser.getId()).isEqualTo(ID_TEST_USER);
    AssertionsForClassTypes.assertThat(testUser.getName()).isEqualTo(NAME_TEST_USER);
    AssertionsForClassTypes.assertThat(testUser.getEmail()).isEqualTo(EMAIL_TEST_USER);
    AssertionsForClassTypes.assertThat(testUser.getDateBirth()).isEqualTo(DATE_BIRTH_TEST_USER);

    testUser.setName("new_name");
    testUser.setEmail("new_email");
    testUser.setDateBirth(LocalDate.of(2020, 2, 2));
    log.info("Save changed test user");
    userRepository.save(testUser);

    log.info("Get test user");
    testUser = userRepository.findById(ID_TEST_USER).orElseThrow();

    AssertionsForClassTypes.assertThat(testUser.getId()).isEqualTo(ID_TEST_USER);
    AssertionsForClassTypes.assertThat(testUser.getName()).isEqualTo("new_name");
    AssertionsForClassTypes.assertThat(testUser.getEmail()).isEqualTo("new_email");
    AssertionsForClassTypes.assertThat(testUser.getDateBirth()).isEqualTo(LocalDate.of(2020, 2, 2));

    deleteUserEntity_TestUser();

    log.info("End TEST baseTableTest_user");
  }

  /**
   * <p>
   *   Тестирует дополнительные методы репозитория {@link UserRepository}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>{@code findByEmail(email)}</li>
   *   <li>{@code existsByEmail(email)}</li>
   *   <li>{@code existsByIdIsNotAndEmail(id, email)}</li>
   *   <li>{@code getSuperAdmin()}</li>
   * </ol>
   */
  @Test
  public void baseRepositoryTest_customMethods() {

    log.info("Start TEST baseRepositoryTest_customMethods");

    createUserEntity_TestUser();

    log.info("Find user bu id");
    UserEntity testUser = userRepository.findById(ID_TEST_USER).orElseThrow();

    // 1. findByEmail(email)

    log.info("Find user by email");
    AssertionsForClassTypes.assertThat(userRepository.findByEmail(EMAIL_TEST_USER).orElseThrow())
        .withFailMessage(() -> "findByEmail(EMAIL_TEST_USER)").isEqualTo(testUser);
    log.info("Find user by email");
    AssertionsForClassTypes.assertThat(userRepository.findByEmail("Not_existing_email").isEmpty())
        .withFailMessage(() -> "findByEmail(\"Not_existing_email\")").isEqualTo(true);

    // 2. existsByEmail(email)

    log.info("Exists user by email");
    AssertionsForClassTypes.assertThat(userRepository.existsByEmail(EMAIL_TEST_USER))
        .withFailMessage(() -> "existsByEmail(EMAIL_TEST_USER)").isEqualTo(true);
    log.info("Exists user by email");
    AssertionsForClassTypes.assertThat(userRepository.existsByEmail("Not_existing_email"))
        .withFailMessage(() -> "existsByEmail(\"Not_existing_email\")").isEqualTo(false);

    // 3. existsByIdIsNotAndEmail(id, email)

    log.info("Exists user by another id and email");
    AssertionsForClassTypes.assertThat(userRepository.existsByIdIsNotAndEmail(ID_TEST_USER, EMAIL_TEST_USER))
        .withFailMessage(() -> "existsByIdIsNotAndEmail(ID_TEST_USER, EMAIL_TEST_USER)").isEqualTo(false);
    log.info("Exists user by another id and email");
    AssertionsForClassTypes.assertThat(userRepository.existsByIdIsNotAndEmail(ID_TEST_USER, "Not_existing_email"))
        .withFailMessage(() -> "existsByIdIsNotAndEmail(ID_TEST_USER, \"Not_existing_email\")").isEqualTo(false);
    log.info("Exists user by another id and email");
    AssertionsForClassTypes.assertThat(userRepository.existsByIdIsNotAndEmail("Not_existing_id", EMAIL_TEST_USER))
        .withFailMessage(() -> "existsByIdIsNotAndEmail(\"Not_existing_id\", EMAIL_TEST_USER)").isEqualTo(true);
    log.info("Exists user by another id and email");
    AssertionsForClassTypes.assertThat(userRepository.existsByIdIsNotAndEmail("Not_existing_id", "Not_existing_email"))
        .withFailMessage(() -> "existsByIdIsNotAndEmail(\"Not_existing_id\", \"Not_existing_email\")").isEqualTo(false);

    // 4. getSuperAdmin()

    log.info("Get super admin");
    AssertionsForClassTypes.assertThat(roleRepository.findAllByIdUser(
        userRepository.getSuperAdmin().getId()).contains(RDB.ROLE_SUPER_ADMIN))
        .withFailMessage(() -> "getSuperAdmin()").isEqualTo(true);

    deleteUserEntity_TestUser();

    log.info("End TEST baseRepositoryTest_customMethods");
  }



  /**
   * <p>
   *   Тестирует связь <b><i>many-to-many</i></b> таблицы <b>"users_roles"</b> между моделями {@link UserEntity} и {@link RoleEntity}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Создание тестового пользователя.</li>
   *   <li>Создание связи с существующей в бд ролью.</li>
   *   <li>Удаление тестового пользователя, участвующего в связи.</li>
   *   <li>Создание тестового пользователя повторно, добавление связи для её удаления.</li>
   *   <li>Удаление роли после разрыва связи.</li>
   * </ol>
   */
  @Test
  @Transactional
  public void relationTableTest_manyToMany_userRole() {

    log.info("Start TEST relationTableTest_manyToMany_userRole");

    // 1. Создание тестового пользователя.

    createUserEntity_TestUser();
    log.info("All test user's roles (1)");
    AssertionsForClassTypes.assertThat(roleRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(0);

    // 2. Создание связи с существующей в бд ролью.

    RoleEntity testRole = new RoleEntity("id_testRole", "testRole");
    log.info("Save role (2)");
    roleRepository.save(testRole);
    log.info("Add saved role to test user (2)");
    userRepository.addRoleToUser(ID_TEST_USER, testRole.getId());
    log.info("All test user's roles (2)");
    AssertionsForClassTypes.assertThat(roleRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(1);

    // 3. Удаление тестового пользователя, участвующего в связи.

    log.info("Delete test user with relation (3)");
    userRepository.deleteById(ID_TEST_USER);
    log.info("All test user's roles (3)");
    AssertionsForClassTypes.assertThat(roleRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(0);

    // 4. Создание тестового пользователя повторно, добавление связи для её удаления.

    createUserEntity_TestUser();
    log.info("Add saved role to test user (4)");
    userRepository.addRoleToUser(ID_TEST_USER, testRole.getId());
    log.info("All test user's roles (4)");
    AssertionsForClassTypes.assertThat(roleRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(1);
    log.info("Delete test role from test user (4)");
    userRepository.deleteRoleFromUser(ID_TEST_USER, testRole.getId());
    log.info("All test user's roles (4)");
    AssertionsForClassTypes.assertThat(roleRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(0);

    // 5. Удаление роли после разрыва связи.

    log.info("Delete role without relation (5)");
    roleRepository.delete(testRole);

    deleteUserEntity_TestUser();

    log.info("End TEST relationTableTest_manyToMany_userRole");
  }

  /**
   * <p>
   *   Тестирует связь <b><i>many-to-many</i></b> таблицы <b>"users_children"</b> между моделями {@link UserEntity} и {@link ChildBirthEntity}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Создание тестового пользователя.</li>
   *   <li>Создание связи с существующим в бд рождением ребенка.</li>
   *   <li>Удаление тестового пользователя, участвующего в связи.</li>
   *   <li>Создание тестового пользователя повторно, добавление связи для её удаления.</li>
   *   <li>Удаление рождения ребенка после разрыва связи.</li>
   * </ol>
   */
  @Test
  @Transactional
  public void relationTableTest_manyToMany_userChild() {

    log.info("Start TEST relationTableTest_manyToMany_userChild");

    // 1. Создание тестового пользователя.

    createUserEntity_TestUser();
    log.info("All test user's children (1)");
    AssertionsForClassTypes.assertThat(childBirthRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(0);

    // 2. Создание связи с существующей в бд ролью.

    ChildBirthEntity testChild = new ChildBirthEntity("id_testChild", LocalDate.of(2010, 10, 10));
    log.info("Save child (2)");
    childBirthRepository.save(testChild);
    log.info("Add saved child to test user (2)");
    userRepository.addChildToUser(ID_TEST_USER, testChild.getId());
    log.info("All test user's children (2)");
    AssertionsForClassTypes.assertThat(childBirthRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(1);

    // 3. Удаление тестового пользователя, участвующего в связи.

    log.info("Delete test user with relation (3)");
    userRepository.deleteById(ID_TEST_USER);
    log.info("All test user's children (3)");
    AssertionsForClassTypes.assertThat(childBirthRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(0);

    // 4. Создание тестового пользователя повторно, добавление связи для её удаления.

    createUserEntity_TestUser();
    log.info("Add saved child to test user (4)");
    userRepository.addChildToUser(ID_TEST_USER, testChild.getId());
    log.info("All test user's children (4)");
    AssertionsForClassTypes.assertThat(childBirthRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(1);
    log.info("Delete all children from test user (4)");
    userRepository.deleteAllChildrenFromUser(ID_TEST_USER);
    log.info("All test user's children (4)");
    AssertionsForClassTypes.assertThat(childBirthRepository.findAllByIdUser(ID_TEST_USER).size()).isEqualTo(0);

    // 5. Удаление роли после разрыва связи.

    log.info("Delete child without relation (5)");
    childBirthRepository.delete(testChild);

    deleteUserEntity_TestUser();

    log.info("End TEST relationTableTest_manyToMany_userChild");
  }

  /**
   * <p>
   *   Тестирует связь <b><i>many-to-one</i></b> между таблицами <b>"user"</b> и <b>"city"</b>, между моделями {@link UserEntity} и {@link CityEntity}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Создание тестового пользователя.</li>
   *   <li>Создание связи с существующим в бд городом.</li>
   *   <li>Удаление тестового пользователя, участвующего в связи.</li>
   *   <li>Создание тестового пользователя повторно, добавление связи для её удаления.</li>
   *   <li>Удаление города после разрыва связи.</li>
   * </ol>
   */
  @Test
  public void relationTableTest_manyToOne_userCity() {

    log.info("Start TEST relationTableTest_manyToOne_userCity");

    // 1. Создание тестового пользователя.

    createUserEntity_TestUser();
    log.info("Find test user (1)");
    UserEntity testUser = userRepository.findById(ID_TEST_USER).orElseThrow();

    // 2. Создание связи с существующим в бд городом.

    CityEntity testCity = new CityEntity("id_testCity", "testCity", null);
    log.info("Save city (2)");
    cityRepository.save(testCity);
    testUser.setIdCity("id_testCity");
    log.info("Save test user with set saved city (2)");
    userRepository.save(testUser);
    log.info("Find test user's city (2)");
    AssertionsForClassTypes.assertThat(cityRepository.findByIdUser(ID_TEST_USER).get()).isEqualTo(testCity);

    // 3. Удаление тестового пользователя, участвующего в связи.

    log.info("Delete test user with relation (3)");
    userRepository.deleteById(ID_TEST_USER);
    log.info("Find test user's city (3)");
    AssertionsForClassTypes.assertThat(cityRepository.findByIdUser(ID_TEST_USER).isEmpty()).isEqualTo(true);

    // 4. Создание тестового пользователя повторно, добавление связи для её удаления.

    createUserEntity_TestUser();
    log.info("Find test user (4)");
    testUser = userRepository.findById(ID_TEST_USER).orElseThrow();
    testUser.setIdCity("id_testCity");
    log.info("Save test user with set saved city (4)");
    userRepository.save(testUser);
    log.info("Find test user's city (4)");
    AssertionsForClassTypes.assertThat(cityRepository.findByIdUser(ID_TEST_USER).get()).isEqualTo(testCity);
    testUser.setIdCity(null);
    log.info("Save test user without saved city (4)");
    userRepository.save(testUser);
    log.info("Find test user's city (4)");
    AssertionsForClassTypes.assertThat(cityRepository.findByIdUser(ID_TEST_USER).isEmpty()).isEqualTo(true);

    // 5. Удаление города после разрыва связи.

    log.info("Delete city without relation (5)");
    cityRepository.delete(testCity);

    deleteUserEntity_TestUser();

    log.info("End TEST relationTableTest_manyToOne_userCity");
  }

  /**
   * <p>
   *   Тестирует связь <b><i>one-to-one</i></b> между таблицами <b>"user"</b> и <b>"refresh_token"</b>, между моделями {@link UserEntity} и {@link RefreshTokenEntity}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Создание тестового пользователя.</li>
   *   <li>Сохранение токена с id тестового пользователя.</li>
   *   <li>Удаление токена, участвующего в связи.</li>
   *   <li>Сохранение токена с id тестового пользователя.</li>
   *   <li>Удаление тестового пользователя, участвующего в связи.</li>
   * </ol>
   */
  @Test
  public void relationTableTest_oneToOne_userRefresh() {

    log.info("Start TEST relationTableTest_oneToOne_userRefresh");

    // 1. Создание тестового пользователя.

    createUserEntity_TestUser();

    // 2. Сохранение токена с id тестового пользователя.

    RefreshTokenEntity testRefresh = new RefreshTokenEntity(ID_TEST_USER, "testRefresh",
                                                            LocalDateTime.of(2022, 3, 30, 23, 59, 59));
    log.info("Save refresh (2)");
    refreshTokenRepository.save(testRefresh);
    log.info("Find test user's refresh (2)");
    AssertionsForClassTypes.assertThat(refreshTokenRepository.findById(ID_TEST_USER).get()).isEqualTo(testRefresh);

    // 3. Удаление токена, участвующего в связи.

    log.info("Delete refresh with relation (3)");
    refreshTokenRepository.deleteById(ID_TEST_USER);
    log.info("Find test user's refresh (3)");
    AssertionsForClassTypes.assertThat(refreshTokenRepository.findById(ID_TEST_USER).isEmpty()).isEqualTo(true);

    // 4. Сохранение токена с id тестового пользователя.

    log.info("Save refresh (4)");
    refreshTokenRepository.save(testRefresh);
    log.info("Find test user's refresh (4)");
    AssertionsForClassTypes.assertThat(refreshTokenRepository.findById(ID_TEST_USER).get()).isEqualTo(testRefresh);

    // 5. Удаление тестового пользователя, участвующего в связи.

    deleteUserEntity_TestUser();
    log.info("Find test user's refresh (5)");
    AssertionsForClassTypes.assertThat(refreshTokenRepository.findAll().size()).isEqualTo(0);

    log.info("End TEST relationTableTest_oneToOne_userRefresh");
  }

  /**
   * <p>
   *   Тестирует связь <b><i>one-to-one</i></b> между таблицами <b>"user"</b> и <b>"login_code"</b>, между моделями {@link UserEntity} и {@link LoginCodeEntity}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Создание тестового пользователя.</li>
   *   <li>Сохранение кода с id тестового пользователя.</li>
   *   <li>Удаление кода, участвующего в связи.</li>
   *   <li>Сохранение кода с id тестового пользователя.</li>
   *   <li>Удаление тестового пользователя, участвующего в связи.</li>
   * </ol>
   */
  @Test
  public void relationTableTest_oneToOne_userLogin() {

    log.info("Start TEST relationTableTest_oneToOne_userLogin");

    // 1. Создание тестового пользователя.

    createUserEntity_TestUser();

    // 2. Сохранение кода с id тестового пользователя.

    LoginCodeEntity testLogin = new LoginCodeEntity(ID_TEST_USER, 123456,
                                                      LocalDateTime.of(2022, 3, 30, 23, 59, 59));
    log.info("Save login (2)");
    loginCodeRepository.save(testLogin);
    log.info("Find test user's login (2)");
    AssertionsForClassTypes.assertThat(loginCodeRepository.findById(ID_TEST_USER).get()).isEqualTo(testLogin);

    // 3. Удаление кода, участвующего в связи.

    log.info("Delete login with relation (3)");
    loginCodeRepository.deleteById(ID_TEST_USER);
    log.info("Find test user's login (3)");
    AssertionsForClassTypes.assertThat(loginCodeRepository.findById(ID_TEST_USER).isEmpty()).isEqualTo(true);

    // 4. Сохранение кода с id тестового пользователя.

    log.info("Save login (4)");
    loginCodeRepository.save(testLogin);
    log.info("Find test user's login (4)");
    AssertionsForClassTypes.assertThat(loginCodeRepository.findById(ID_TEST_USER).get()).isEqualTo(testLogin);

    // 5. Удаление тестового пользователя, участвующего в связи.

    deleteUserEntity_TestUser();
    log.info("Find test user's login (5)");
    AssertionsForClassTypes.assertThat(loginCodeRepository.findAll().size()).isEqualTo(0);

    log.info("End TEST relationTableTest_oneToOne_userLogin");
  }
}
