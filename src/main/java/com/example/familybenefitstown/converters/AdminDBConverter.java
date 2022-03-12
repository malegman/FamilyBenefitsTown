package com.example.familybenefitstown.converters;

import com.example.familybenefitstown.api_models.admin.AdminInfo;
import com.example.familybenefitstown.api_models.admin.AdminSave;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс преобразования модели таблицы "user" в другие объекты и получения из других объектов, обрабатывая строковые поля для БД.
 * В рамках объекта "администратор"
 */
public class AdminDBConverter {

  /**
   * Преобразует объект запроса на сохранение администратора в модель таблицы "user", обрабатывая строковые поля для БД
   * @param adminSave объект запроса на сохранение администратора
   * @param prepareDBFunc функция обработки строки для БД
   * @return модель таблицы "user"
   */
  static public UserEntity fromSave(AdminSave adminSave, Function<String, String> prepareDBFunc) {

    if (adminSave == null) {
      return new UserEntity();
    }

    return UserEntity
        .builder()
        .name(prepareDBFunc.apply(adminSave.getName()))
        .email(prepareDBFunc.apply(adminSave.getEmail()))
        .build();
  }

  /**
   * Преобразует модель таблицы "user" в объект информации об администраторе
   * @param userEntity модель таблицы "user"
   * @return информация об администраторе
   */
  static public AdminInfo toInfo(UserEntity userEntity) {

    if (userEntity == null) {
      return new AdminInfo();
    }

    return AdminInfo
        .builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .email(userEntity.getEmail())
        .nameRoleSet(userEntity.getRoleEntityList()
                         .stream()
                         .map(RoleEntity::getName)
                         .collect(Collectors.toList()))
        .build();
  }
}
