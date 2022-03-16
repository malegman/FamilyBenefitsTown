package com.example.familybenefitstown.dto.repositories;

import com.example.familybenefitstown.dto.entities.ChildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий, работающий с моделью таблицы "child"
 */
public interface ChildRepository extends JpaRepository<ChildEntity, String> {

  /**
   * Находит ребенка по дате рождения
   * @param dateBirth дата рождения
   * @return модель ребенка или {@code empty}, если ребенок не найден
   */
  Optional<ChildEntity> findByDateBirth(LocalDate dateBirth);

  /**
   * Возвращает список детей по ID пользователя
   * @param idUser ID пользователя
   * @return список детей
   */
  @Query(nativeQuery = true,
      value = "SELECT family_benefit_town.child.id, family_benefit_town.child.date_birth " +
          "FROM family_benefit_town.users_children " +
          "INNER JOIN family_benefit_town.child ON family_benefit_town.users_children.id_child = family_benefit_town.child.id " +
          "WHERE family_benefit_town.users_children.id_user = ?;")
  List<ChildEntity> findAllByIdUser(String idUser);
}
