package com.example.familybenefitstown.repositories;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.dto.entities.ChildEntity;
import com.example.familybenefitstown.dto.repositories.ChildRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FamilyBenefitsTownApplication.class})
public class ChildRepositoryTest {

  @Autowired
  private ChildRepository childRepository;

  private static final String ID_TEST_CHILD = "id_test_child";
  private static final LocalDate BIRTH_TEST_CHILD = LocalDate.of(2010, 10, 10);

  /**
   * Создает тестового ребенка перед каждым тестом
   */
  private void createChildEntity_TestChild() {

    log.info("Start createChildEntity_TestChild");

    log.info("Save test child");
    childRepository.save(ChildEntity
                             .builder()
                             .id(ID_TEST_CHILD)
                             .dateBirth(BIRTH_TEST_CHILD)
                             .build());

    log.info("End createChildEntity_TestChild");
  }

  /**
   * Удаляет тестового ребенка после каждого теста
   */
  private void deleteChildEntity_TestChild() {

    log.info("Start deleteChildEntity_TestChild");

    log.info("Exists test child");
    if (childRepository.existsById(ID_TEST_CHILD)) {
      log.info("Delete test child");
      childRepository.deleteById(ID_TEST_CHILD);
    }

    log.info("End deleteChildEntity_TestChild");
  }

  /**
   * <p>
   *   Тестирует таблицу роли <b>"role"</b> по модели {@link ChildEntity}, без связанных таблиц, сущностей.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>Получение тестовой записи и всех её полей.</li>
   * </ol>
   */
  @Test
  public void baseTableTest_child() {

    log.info("Start TEST baseTableTest_child");

    createChildEntity_TestChild();

    log.info("Get test child");
    ChildEntity testChild = childRepository.findById(ID_TEST_CHILD).orElseThrow();

    AssertionsForClassTypes.assertThat(testChild.getId()).isEqualTo(ID_TEST_CHILD);
    AssertionsForClassTypes.assertThat(testChild.getDateBirth()).isEqualTo(BIRTH_TEST_CHILD);

    testChild.setDateBirth(LocalDate.of(2020, 1, 5));
    log.info("Save changed test child");
    childRepository.save(testChild);

    log.info("Get test child");
    testChild = childRepository.findById(ID_TEST_CHILD).orElseThrow();

    AssertionsForClassTypes.assertThat(testChild.getId()).isEqualTo(ID_TEST_CHILD);
    AssertionsForClassTypes.assertThat(testChild.getDateBirth()).isEqualTo(LocalDate.of(2020, 1, 5));

    deleteChildEntity_TestChild();

    log.info("End TEST baseTableTest_child");
  }

  /**
   * <p>
   *   Тестирует дополнительные методы репозитория {@link ChildRepository}.
   * </p>
   * <p>
   *   Порядок тестирования:
   * </p>
   * <ol>
   *   <li>{@code findByDateBirth(dateBirth)}</li>
   * </ol>
   */
  @Test
  public void baseRepositoryTest_customMethods() {

    log.info("Start TEST baseRepositoryTest_customMethods");

    createChildEntity_TestChild();

    // 1. findByDateBirth(dateBirth)

    log.info("Get test child");
    ChildEntity testChild = childRepository.findById(ID_TEST_CHILD).orElseThrow();

    log.info("Find by existing date");
    AssertionsForClassTypes.assertThat(childRepository.findByDateBirth(BIRTH_TEST_CHILD).orElseThrow()).isEqualTo(testChild);
    log.info("Find by not existing date");
    AssertionsForClassTypes.assertThat(childRepository.findByDateBirth(LocalDate.of(2020, 1, 5)).isEmpty()).isEqualTo(true);

    deleteChildEntity_TestChild();

    log.info("End TEST baseRepositoryTest_customMethods");
  }
}
