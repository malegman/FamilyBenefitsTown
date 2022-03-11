package com.example.familybenefitstown.dto.repositories.weak;

import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.dto.entities.weak.UsersChildrenEntity;
import com.example.familybenefitstown.dto.entities.weak.keys.UsersChildrenKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий, работающий с моделью таблицы "users_children"
 */
public interface UsersChildrenRepository extends JpaRepository<UsersChildrenEntity, UsersChildrenKey> {

  /**
   * Возвращает список детей пользователя
   * @param userEntity модель пользователя
   * @return список детей
   */
  List<UsersChildrenEntity> findAllByUserEntity(UserEntity userEntity);
}
