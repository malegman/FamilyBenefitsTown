package com.example.familybenefitstown.dto.repositories.weak;

import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.dto.entities.weak.UsersRolesEntity;
import com.example.familybenefitstown.dto.entities.weak.keys.UsersRolesKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий, работающий с моделью таблицы "users_roles"
 */
public interface UsersRolesRepository extends JpaRepository<UsersRolesEntity, UsersRolesKey> {

  /**
   * Возвращает список ролей пользователя
   * @param userEntity модель пользователя
   * @return список ролей
   */
  List<UsersRolesEntity> findAllByUserEntity(UserEntity userEntity);
}
