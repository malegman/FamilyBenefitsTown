package com.example.familybenefitstown.repositories;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.res_part_rest_api.dto.entities.RefreshTokenEntity;
import com.example.familybenefitstown.res_part_rest_api.dto.entities.UserEntity;
import com.example.familybenefitstown.res_part_rest_api.dto.repositories.RefreshTokenRepository;
import com.example.familybenefitstown.res_part_rest_api.dto.repositories.UserRepository;
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
public class RefreshTokenRepositoryTest {

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  private static final String ID_TEST_USER = "id_test_user";
  private static final String TOKEN_TEST_REFRESH = "testToken";
  private static final LocalDateTime DATE_TIME_EXP_TEST_REFRESH = LocalDateTime.of(1990, 10, 10, 20, 20, 20);

  private static final String EMAIL_TEST_USER = "testUser@mail.com";
  private static final String NAME_TEST_USER = "testUser";
  private static final LocalDate DATE_BIRTH_TEST_USER = LocalDate.of(1990, 10, 10);

  /**
   * Создает тестовый токен перед каждым тестом
   */
  private void createRefreshEntity_TestRefresh() {

    log.info("Start createRefreshEntity_TestRefresh");

    log.info("Save test refresh");
    refreshTokenRepository.save(RefreshTokenEntity
                                    .builder()
                                    .idUser(ID_TEST_USER)
                                    .token(TOKEN_TEST_REFRESH)
                                    .dateExpiration(DATE_TIME_EXP_TEST_REFRESH)
                                    .build());

    log.info("End createRefreshEntity_TestRefresh");
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
   *   Тестирует таблицу токена восстановления <b>"refresh_token"</b> по модели {@link RefreshTokenEntity}, без связанных таблиц, сущностей.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Получение тестовой записи и всех её полей.</li>
   * </ol>
   */
  @Test
  public void baseTableTest_refresh() {

    log.info("Start TEST baseTableTest_refresh");

    createUserEntity_TestUser();
    createRefreshEntity_TestRefresh();

    log.info("Get test refresh");
    RefreshTokenEntity testRefresh = refreshTokenRepository.findById(ID_TEST_USER).orElseThrow();

    AssertionsForClassTypes.assertThat(testRefresh.getIdUser()).isEqualTo(ID_TEST_USER);
    AssertionsForClassTypes.assertThat(testRefresh.getToken()).isEqualTo(TOKEN_TEST_REFRESH);
    AssertionsForClassTypes.assertThat(testRefresh.getDateExpiration()).isEqualTo(DATE_TIME_EXP_TEST_REFRESH);

    testRefresh.setToken("new_token");
    testRefresh.setDateExpiration(LocalDateTime.of(2020, 5, 20, 10, 10, 10));
    log.info("Save changed test refresh");
    refreshTokenRepository.save(testRefresh);

    log.info("Get test refresh after set");
    testRefresh = refreshTokenRepository.findById(ID_TEST_USER).orElseThrow();

    AssertionsForClassTypes.assertThat(testRefresh.getIdUser()).isEqualTo(ID_TEST_USER);
    AssertionsForClassTypes.assertThat(testRefresh.getToken()).isEqualTo("new_token");
    AssertionsForClassTypes.assertThat(testRefresh.getDateExpiration())
        .isEqualTo(LocalDateTime.of(2020, 5, 20, 10, 10, 10));

    deleteUserEntity_TestUser();

    log.info("End TEST baseTableTest_refresh");
  }

  /**
   * <p>
   *   Тестирует дополнительные методы репозитория {@link RefreshTokenRepository}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>{@code findByToken(refreshToken)}</li>
   *   <li>{@code deleteByToken(refreshToken)}</li>
   * </ol>
   */
  @Test
  @Transactional
  public void baseRepositoryTest_customMethods() {

    log.info("Start TEST baseRepositoryTest_customMethods");

    createUserEntity_TestUser();
    createRefreshEntity_TestRefresh();

    // 1. findByToken(refreshToken)

    log.info("Get test refresh");
    RefreshTokenEntity testRefresh = refreshTokenRepository.findById(ID_TEST_USER).orElseThrow();

    log.info("Find by existing token");
    AssertionsForClassTypes.assertThat(refreshTokenRepository.findByToken(TOKEN_TEST_REFRESH).orElseThrow()).isEqualTo(testRefresh);
    log.info("Find by not existing token");
    AssertionsForClassTypes.assertThat(refreshTokenRepository.findByToken("Not_existing_token").isEmpty()).isEqualTo(true);

    // 2. deleteByToken(refreshToken)

    log.info("Delete test refresh");
    refreshTokenRepository.deleteByToken(TOKEN_TEST_REFRESH);
    log.info("Exists refresh");
    AssertionsForClassTypes.assertThat(refreshTokenRepository.existsById(ID_TEST_USER)).isEqualTo(false);

    deleteUserEntity_TestUser();

    log.info("End TEST baseRepositoryTest_customMethods");
  }
}
