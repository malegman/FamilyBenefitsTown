package com.example.familybenefitstown.dto.repositories;

import com.example.familybenefitstown.dto.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Репозиторий, работающий с моделью таблицы "role"
 */
public interface RoleRepository extends JpaRepository<RoleEntity, String> {

  /**
   * Возвращает список ролей по ID пользователя
   * @param idUser ID пользователя
   * @return множество ролей
   */
  @Query(nativeQuery = true,
      value = "SELECT family_benefit_town.role.id, family_benefit_town.role.name " +
          "FROM family_benefit_town.users_roles INNER JOIN family_benefit_town.role ON family_benefit_town.users_roles.id_role = family_benefit_town.role.id " +
          "WHERE family_benefit_town.users_roles.id_user = ?;")
  List<RoleEntity> findAllByIdUser(String idUser);
}
