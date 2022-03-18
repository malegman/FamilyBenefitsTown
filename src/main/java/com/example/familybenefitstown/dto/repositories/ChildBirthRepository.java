package com.example.familybenefitstown.dto.repositories;

import com.example.familybenefitstown.dto.entities.ChildBirthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий, работающий с моделью таблицы "child_birth"
 */
public interface ChildBirthRepository extends JpaRepository<ChildBirthEntity, String> {

  /**
   * Находит рождение ребенка по дате рождения
   * @param dateBirth дата рождения
   * @return модель рождения ребенка или {@code empty}, если рождение не найдено
   */
  Optional<ChildBirthEntity> findByDateBirth(LocalDate dateBirth);

  /**
   * Возвращает список рождений детей по ID пользователя
   * @param idUser ID пользователя
   * @return список рождений детей
   */
  @Query(nativeQuery = true,
      value = "SELECT family_benefit_town.child_birth.id, family_benefit_town.child_birth.date_birth " +
          "FROM family_benefit_town.users_children " +
          "INNER JOIN family_benefit_town.child_birth ON family_benefit_town.users_children.id_child_birth = family_benefit_town.child_birth.id " +
          "WHERE family_benefit_town.users_children.id_user = ?;")
  List<ChildBirthEntity> findAllByIdUser(String idUser);
}
