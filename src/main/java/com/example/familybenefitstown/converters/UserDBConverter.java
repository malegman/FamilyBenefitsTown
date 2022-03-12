package com.example.familybenefitstown.converters;

import com.example.familybenefitstown.api_models.user.UserInfo;
import com.example.familybenefitstown.api_models.user.UserSave;
import com.example.familybenefitstown.dto.entities.strong.ChildEntity;
import com.example.familybenefitstown.dto.entities.strong.CityEntity;
import com.example.familybenefitstown.dto.entities.strong.RoleEntity;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.resources.R;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс преобразования модели таблицы "user" в другие объекты и получения из других объектов, обрабатывая строковые поля для БД.
 * В рамках объекта "пользователь"
 */
public class UserDBConverter {

  /**
   * Преобразует объект запроса на сохранение пользователя в модель таблицы "user", обрабатывая строковые поля для БД.
   * В преобразовании не участвуют поля с датами рождениями детей и датой рождения пользователя
   * @param userSave объект запроса на сохранение пользователя
   * @param prepareDBFunc функция обработки строки для БД
   * @return модель таблицы "user"
   */
  static public UserEntity fromSave(UserSave userSave, Function<String, String> prepareDBFunc) {

    if (userSave == null) {
      return new UserEntity();
    }

    return UserEntity
        .builder()
        .name(prepareDBFunc.apply(userSave.getName()))
        .email(prepareDBFunc.apply(userSave.getEmail()))
        .cityEntity(new CityEntity(prepareDBFunc.apply(userSave.getIdCity())))
        .build();
  }

  /**
   * Преобразует модель таблицы "user" в объект информации о пользователе.
   * В преобразовании не участвуют поля с датами рождениями детей и датой рождения пользователя
   * @param userEntity модель таблицы "user"
   * @return информация о пользователе
   */
  static public UserInfo toInfo(UserEntity userEntity, List<ChildEntity> birthDateChildren, List<RoleEntity> roleEntityList) {

    if (userEntity == null || userEntity.getCityEntity() == null) {
      return new UserInfo();
    }

    return UserInfo
        .builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .email(userEntity.getEmail())
        .birthDateChildren(birthDateChildren
                               .stream()
                               .map(childEntity -> R.SIMPLE_DATE_FORMAT.format(childEntity.getDateBirth()))
                               .collect(Collectors.toList()))
        .nameRoleSet(roleEntityList
                         .stream()
                         .map(RoleEntity::getName)
                         .collect(Collectors.toList()))
        .nameCity(userEntity.getCityEntity().getName())
        .build();
  }
}

