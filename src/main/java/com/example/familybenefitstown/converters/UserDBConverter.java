package com.example.familybenefitstown.converters;

import com.example.familybenefitstown.api_models.user.UserInfo;
import com.example.familybenefitstown.api_models.user.UserSave;
import com.example.familybenefitstown.dto.entities.ChildEntity;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
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
        .idCity(prepareDBFunc.apply(userSave.getIdCity()))
        .build();
  }

  /**
   * Преобразует модель таблицы "user" в объект информации о пользователе.
   * В преобразовании не участвуют поля с датами рождениями детей и датой рождения пользователя
   * @param userEntity модель таблицы "user"
   * @param childEntityList список моделей таблицы "child", связанных с пользователем
   * @param roleEntityList список моделей таблицы "role", связанных с пользователем
   * @param nameCity название города пользователя
   * @return информация о пользователе
   */
  static public UserInfo toInfo(UserEntity userEntity, List<ChildEntity> childEntityList, List<RoleEntity> roleEntityList, String nameCity) {

    if (userEntity == null) {
      return new UserInfo();
    }

    return UserInfo
        .builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .email(userEntity.getEmail())
        .birthDateChildren(childEntityList
                               .stream()
                               .map(childEntity -> R.SIMPLE_DATE_FORMAT.format(childEntity.getDateBirth()))
                               .collect(Collectors.toList()))
        .nameRoleList(roleEntityList
                         .stream()
                         .map(RoleEntity::getName)
                         .collect(Collectors.toList()))
        .nameCity(nameCity)
        .build();
  }
}

