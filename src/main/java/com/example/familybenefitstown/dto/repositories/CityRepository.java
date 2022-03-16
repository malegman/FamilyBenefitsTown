package com.example.familybenefitstown.dto.repositories;

import com.example.familybenefitstown.dto.entities.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

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

  /**
   * Возвращает город пользователя по его ID
   * @param idUser ID пользователя
   * @return город пользователя, или {@code empty} если не найден город указанного пользователя
   */
  @Query(nativeQuery = true,
      value = "SELECT family_benefit_town.city.id, family_benefit_town.city.name, family_benefit_town.city.info " +
          "FROM family_benefit_town.user " +
          "INNER JOIN family_benefit_town.city ON family_benefit_town.user.id_city = family_benefit_town.city.id " +
          "WHERE family_benefit_town.user.id = ?;")
  Optional<CityEntity> findByIdUser(String idUser);
}
