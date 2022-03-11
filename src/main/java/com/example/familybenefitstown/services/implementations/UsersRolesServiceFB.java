package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.dto.entities.strong.RoleEntity;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.dto.entities.weak.UsersRolesEntity;
import com.example.familybenefitstown.dto.entities.weak.keys.UsersRolesKey;
import com.example.familybenefitstown.dto.repositories.weak.UsersRolesRepository;
import com.example.familybenefitstown.services.interfaces.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, управляющего связью пользователей и ролей
 */
@Service
public class UsersRolesServiceFB implements UsersRolesService {

  /**
   * Репозиторий, работающий с моделью таблицы "users_roles"
   */
  private final UsersRolesRepository usersRolesRepository;

  /**
   * Конструктор для инициализации репозитория
   * @param usersRolesRepository репозиторий, работающий с моделью таблицы "users_roles"
   */
  @Autowired
  public UsersRolesServiceFB(UsersRolesRepository usersRolesRepository) {
    this.usersRolesRepository = usersRolesRepository;
  }

  /**
   * Удаляет роль у пользователя
   * @param idUser ID пользователя, у которого необходимо удалить роль
   * @param idRole ID удаляемой роли
   */
  @Override
  public void deleteUserRole(String idUser, String idRole) {

    usersRolesRepository.deleteById(new UsersRolesKey(idUser, idRole));
  }

  /**
   * Проверяет наличие у пользователя роли
   * @param idUser ID пользователя, у которого необходимо проверить наличие роль
   * @param idRole ID проверяемой роли
   * @return true, если пользователь имеет роль
   */
  @Override
  public boolean hasUserRole(String idUser, String idRole) {

    return usersRolesRepository.existsById(new UsersRolesKey(idUser, idRole));
  }

  /**
   * Создает связь между пользователем и данной ролью, добавляет пользователю роль
   * @param userEntity модель пользователя, которому добавляется роль
   * @param roleEntity модель добавляемой роли
   */
  @Override
  public void addUserRole(UserEntity userEntity, RoleEntity roleEntity) {

    usersRolesRepository.saveAndFlush(UsersRolesEntity
                                          .builder()
                                          .id(new UsersRolesKey(userEntity.getId(), roleEntity.getId()))
                                          .userEntity(userEntity)
                                          .roleEntity(roleEntity)
                                          .build());
  }

  /**
   * Возвращает роли пользователя
   * @param userEntity пользователь, роли которого необходимо определить
   * @return роли пользователя
   */
  @Override
  public List<RoleEntity> getRolesByUser(UserEntity userEntity) {

    return usersRolesRepository.findAllByUserEntity(userEntity)
        .stream()
        .map(UsersRolesEntity::getRoleEntity)
        .collect(Collectors.toList());
  }
}
