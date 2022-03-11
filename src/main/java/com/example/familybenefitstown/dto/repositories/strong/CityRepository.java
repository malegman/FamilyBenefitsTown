package com.example.familybenefitstown.dto.repositories.strong;

import com.example.familybenefitstown.dto.entities.strong.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий, работающий с моделью таблицы "city"
 */
public interface CityRepository extends JpaRepository<CityEntity, String> {

  /**
   * Проверяет наличие города по его названию
   * @param name название города
   * @return true, если город с указанным именем существует
   */
  boolean existsByName(String name);

  /**
   * Проверяет наличие города с отличным от данного ID и данным названием
   * @param id ID города
   * @param name название города
   * @return true, если город с отличным ID и указанным названием существует
   */
  boolean existsByIdIsNotAndName(String id, String name);
}
