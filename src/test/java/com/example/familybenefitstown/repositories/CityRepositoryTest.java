package com.example.familybenefitstown.repositories;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.dto.entities.CityEntity;
import com.example.familybenefitstown.dto.repositories.CityRepository;
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
public class CityRepositoryTest {

  @Autowired
  private CityRepository cityRepository;

  private static final String ID_TEST_CITY = "id_test_city";
  private static final String NAME_TEST_CITY = "testCity";
  private static final String INFO_TEST_CITY = "testInfo";

  /**
   * Создает тестовый город перед каждым тестом
   */
  private void createCityEntity_TestCity() {

    log.info("Start createCityEntity_TestCity");

    log.info("Save test city");
    cityRepository.save(CityEntity.builder()
                            .id(ID_TEST_CITY)
                            .name(NAME_TEST_CITY)
                            .info(INFO_TEST_CITY)
                            .build());

    log.info("End createCityEntity_TestCity");
  }

  /**
   * Удаляет тестовый город после каждого теста
   */
  private void deleteCityEntity_TestCity() {

    log.info("Start deleteCityEntity_TestCity");

    log.info("Exists test city");
    if (cityRepository.existsById(ID_TEST_CITY)) {
      log.info("Delete test city");
      cityRepository.deleteById(ID_TEST_CITY);
    }

    log.info("End deleteCityEntity_TestCity");
  }

  /**
   * <p>
   *   Тестирует таблицу города <b>"city"</b> по модели {@link CityEntity}, без связанных таблиц, сущностей.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Получение тестовой записи и всех её полей.</li>
   * </ol>
   */
  @Test
  public void baseTableTest_city() {

    log.info("Start TEST baseTableTest_city");

    createCityEntity_TestCity();

    log.info("Get test city");
    CityEntity testCity = cityRepository.findById(ID_TEST_CITY).orElseThrow();

    AssertionsForClassTypes.assertThat(testCity.getId()).isEqualTo(ID_TEST_CITY);
    AssertionsForClassTypes.assertThat(testCity.getName()).isEqualTo(NAME_TEST_CITY);
    AssertionsForClassTypes.assertThat(testCity.getInfo()).isEqualTo(INFO_TEST_CITY);

    testCity.setName("new name");
    testCity.setInfo("new info");
    log.info("Save changed test city");
    cityRepository.save(testCity);

    log.info("Get test refresh");
    testCity = cityRepository.findById(ID_TEST_CITY).orElseThrow();

    AssertionsForClassTypes.assertThat(testCity.getId()).isEqualTo(ID_TEST_CITY);
    AssertionsForClassTypes.assertThat(testCity.getName()).isEqualTo("new name");
    AssertionsForClassTypes.assertThat(testCity.getInfo()).isEqualTo("new info");

    deleteCityEntity_TestCity();

    log.info("End TEST baseTableTest_city");
  }

  /**
   * <p>
   *   Тестирует дополнительные методы репозитория {@link CityRepository}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>{@code existsByName(name)}</li>
   *   <li>{@code existsByIdIsNotAndName(id, name)}</li>
   * </ol>
   */
  @Test
  public void baseRepositoryTest_customMethods() {

    log.info("Start TEST baseRepositoryTest_customMethods");

    createCityEntity_TestCity();

    // 2. existsByName(name)

    log.info("Exists city by name");
    AssertionsForClassTypes.assertThat(cityRepository.existsByName(NAME_TEST_CITY))
        .withFailMessage(() -> "existsByName(NAME_TEST_CITY)").isEqualTo(true);
    log.info("Exists city by name");
    AssertionsForClassTypes.assertThat(cityRepository.existsByName("Not_existing_name"))
        .withFailMessage(() -> "existsByName(\"Not_existing_name\")").isEqualTo(false);

    // 3. existsByIdIsNotAndName(id, name)

    log.info("Exists city by another id and name");
    AssertionsForClassTypes.assertThat(cityRepository.existsByIdIsNotAndName(ID_TEST_CITY, NAME_TEST_CITY))
        .withFailMessage(() -> "existsByIdIsNotAndName(ID_TEST_CITY, NAME_TEST_CITY)").isEqualTo(false);
    log.info("Exists city by another id and name");
    AssertionsForClassTypes.assertThat(cityRepository.existsByIdIsNotAndName(ID_TEST_CITY, "Not_existing_name"))
        .withFailMessage(() -> "existsByIdIsNotAndName(ID_TEST_CITY, \"Not_existing_name\")").isEqualTo(false);
    log.info("Exists city by another id and name");
    AssertionsForClassTypes.assertThat(cityRepository.existsByIdIsNotAndName("Not_existing_id", NAME_TEST_CITY))
        .withFailMessage(() -> "existsByIdIsNotAndName(\"Not_existing_id\", NAME_TEST_CITY)").isEqualTo(true);
    log.info("Exists city by another id and name");
    AssertionsForClassTypes.assertThat(cityRepository.existsByIdIsNotAndName("Not_existing_id", "Not_existing_name"))
        .withFailMessage(() -> "existsByIdIsNotAndName(\"Not_existing_id\", \"Not_existing_name\")").isEqualTo(false);

    deleteCityEntity_TestCity();

    log.info("End TEST baseRepositoryTest_customMethods");
  }
}
