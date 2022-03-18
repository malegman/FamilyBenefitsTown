package com.example.familybenefitstown.repositories;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.dto.entities.LoginCodeEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.dto.repositories.LoginCodeRepository;
import com.example.familybenefitstown.dto.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FamilyBenefitsTownApplication.class})
public class LoginCodeRepositoryTest {

  @Autowired
  private LoginCodeRepository loginCodeRepository;

  @Autowired
  private UserRepository userRepository;

  private static final String ID_TEST_USER = "id_test_user";
  private static final int CODE_TEST_LOGIN = 123456;
  private static final LocalDateTime DATE_TIME_EXP_TEST_LOGIN = LocalDateTime.of(1990, 10, 10, 20, 20, 20);

  private static final String EMAIL_TEST_USER = "testUser@mail.com";
  private static final String NAME_TEST_USER = "testUser";
  private static final LocalDate DATE_BIRTH_TEST_USER = LocalDate.of(1990, 10, 10);

  /**
   * Создает тестовый код перед каждым тестом
   */
  private void createLoginEntity_TestLogin() {

    log.info("Start createLoginEntity_TestLogin");

    log.info("Save test login");
    loginCodeRepository.save(LoginCodeEntity
                                 .builder()
                                 .idUser(ID_TEST_USER)
                                 .code(CODE_TEST_LOGIN)
                                 .dateExpiration(DATE_TIME_EXP_TEST_LOGIN)
                                 .build());

    log.info("End createLoginEntity_TestLogin");
  }

  /**
   * Создает тестового пользователя перед каждым тестом
   */
  private void createUserEntity_TestUser() {

    log.info("Start createUserEntity_TestUser");

    log.info("Save testUser");
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
      log.info("Delete testUser");
      userRepository.deleteById(ID_TEST_USER);
    }

    log.info("End deleteUserEntity_TestUser");
  }

  /**
   * <p>
   *   Тестирует таблицу кода входа <b>"login_code"</b> по модели {@link LoginCodeEntity}, без связанных таблиц, сущностей.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Получение тестовой записи и всех её полей.</li>
   * </ol>
   */
  @Test
  public void baseTableTest_login() {

    log.info("Start TEST baseTableTest_login");

    createUserEntity_TestUser();
    createLoginEntity_TestLogin();

    log.info("Get test refresh");
    LoginCodeEntity testLogin = loginCodeRepository.findById(ID_TEST_USER).orElseThrow();

    AssertionsForClassTypes.assertThat(testLogin.getIdUser()).isEqualTo(ID_TEST_USER);
    AssertionsForClassTypes.assertThat(testLogin.getCode()).isEqualTo(CODE_TEST_LOGIN);
    AssertionsForClassTypes.assertThat(testLogin.getDateExpiration()).isEqualTo(DATE_TIME_EXP_TEST_LOGIN);

    testLogin.setCode(777777);
    testLogin.setDateExpiration(LocalDateTime.of(2020, 5, 20, 10, 10, 10));
    log.info("Save changed test refresh");
    loginCodeRepository.save(testLogin);

    log.info("Get test refresh");
    testLogin = loginCodeRepository.findById(ID_TEST_USER).orElseThrow();

    AssertionsForClassTypes.assertThat(testLogin.getIdUser()).isEqualTo(ID_TEST_USER);
    AssertionsForClassTypes.assertThat(testLogin.getCode()).isEqualTo(777777);
    AssertionsForClassTypes.assertThat(testLogin.getDateExpiration())
        .isEqualTo(LocalDateTime.of(2020, 5, 20, 10, 10, 10));

    deleteUserEntity_TestUser();

    log.info("End TEST baseTableTest_login");
  }

  /**
   * <p>
   *   Тестирует дополнительные методы репозитория {@link LoginCodeRepository}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>{@code findByCode(code)}</li>
   * </ol>
   */
  @Test
  public void baseRepositoryTest_customMethods() {

    log.info("Start TEST baseRepositoryTest_customMethods");

    createUserEntity_TestUser();
    createLoginEntity_TestLogin();

    // 1. findByCode(code)

    log.info("Get test refresh");
    LoginCodeEntity testLogin = loginCodeRepository.findById(ID_TEST_USER).orElseThrow();

    log.info("Find by existing code");
    AssertionsForClassTypes.assertThat(loginCodeRepository.findByCode(CODE_TEST_LOGIN).orElseThrow()).isEqualTo(testLogin);
    log.info("Find by not existing code");
    AssertionsForClassTypes.assertThat(loginCodeRepository.findByCode(777777).isEmpty()).isEqualTo(true);

    deleteUserEntity_TestUser();

    log.info("End TEST baseRepositoryTest_customMethods");
  }
}
