package com.example.familybenefitstown.security.services.interfaces;

import com.example.familybenefitstown.dto.entity.RoleEntity;
import com.example.familybenefitstown.dto.entity.UserEntity;
import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.exceptions.UserRoleException;

/**
 * Интерфейс сервиса, отвечающего за данные пользователя
 */
public interface UserSecurityService {

  /**
   * Проверяет корректность email
   * @param email проверяемый email
   * @throws InvalidEmailException если указанный "email" не является email
   */
  void checkEmailElseThrow(String email) throws InvalidEmailException;

  /**
   * Проверяет наличие указанной роли у указанной модели таблицы "user"
   * @param userEntity модель таблицы "user", роль которой необходимо проверить
   * @param role проверяемая роль
   * @param nameTypeObject название проверяемого объекта
   * @throws NotFoundException если модель не имеет роль и связано с отсутствием объекта в бд
   */
  void checkHasRoleElseThrowNotFound(UserEntity userEntity, RoleEntity role, String nameTypeObject) throws NotFoundException;

  /**
   * Проверяет наличие указанной роли у указанной модели таблицы "user"
   * @param userEntity модель таблицы "user", роль которой необходимо проверить
   * @param role проверяемая роль
   * @param nameTypeObject название проверяемого объекта
   * @throws UserRoleException если модель имеет роль
   */
  void checkNotHasRoleElseThrowUserRole(UserEntity userEntity, RoleEntity role, String nameTypeObject) throws UserRoleException;
}
