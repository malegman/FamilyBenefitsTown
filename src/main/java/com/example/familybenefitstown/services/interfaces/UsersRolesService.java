package com.example.familybenefitstown.services.interfaces;

import com.example.familybenefitstown.dto.entities.strong.RoleEntity;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;

import java.util.List;

/**
 * Интерфейс сервиса, управляющего связью пользователей и ролей
 */
public interface UsersRolesService {

  /**
   * Удаляет роль у пользователя
   * @param idUser ID пользователя, у которого необходимо удалить роль
   * @param idRole ID удаляемой роли
   */
  void deleteUserRole(String idUser, String idRole);

  /**
   * Проверяет наличие у пользователя роли
   * @param idUser ID пользователя, у которого необходимо проверить наличие роль
   * @param idRole ID проверяемой роли
   * @return true, если пользователь имеет роль
   */
  boolean hasUserRole(String idUser, String idRole);

  /**
   * Создает связь между пользователем и данной ролью, добавляет пользователю роль
   * @param userEntity модель пользователя, которому добавляется роль
   * @param roleEntity модель добавляемой роли
   */
  void addUserRole(UserEntity userEntity, RoleEntity roleEntity);

  /**
   * Возвращает роли пользователя
   * @param userEntity пользователь, роли которого необходимо определить
   * @return роли пользователя
   */
  List<RoleEntity> getRolesByUser(UserEntity userEntity);
}
