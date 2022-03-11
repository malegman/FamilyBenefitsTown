package com.example.familybenefitstown.dto.repositories.strong;

import com.example.familybenefitstown.dto.entities.strong.ChildEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
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
}
