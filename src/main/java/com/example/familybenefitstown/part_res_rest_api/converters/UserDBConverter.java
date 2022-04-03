package com.example.familybenefitstown.part_res_rest_api.converters;

import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.user.UserSave;
import com.example.familybenefitstown.dto.entities.ChildBirthEntity;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.security.RandomValue;

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
   * @param idUser ID пользователя. Если {@code null}, значение ID генерируется.
   * @param userSave объект запроса на сохранение пользователя
   * @param prepareDBFunc функция обработки строки для БД
   * @return модель таблицы "user"
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  public static UserEntity fromSave(String idUser, UserSave userSave, Function<String, String> prepareDBFunc) throws InvalidStringException {

    if (userSave == null) {
      return new UserEntity();
    }

    return UserEntity
        .builder()
        .id(idUser != null
                ? prepareDBFunc.apply(idUser)
                : RandomValue.randomString(R.ID_LENGTH))
        .name(prepareDBFunc.apply(FieldConverter.withSymbolsField(userSave.getName(), "name", true)))
        .email(prepareDBFunc.apply(FieldConverter.withSymbolsField(userSave.getEmail(), "email", true)))
        .idCity(prepareDBFunc.apply(FieldConverter.withSymbolsField(userSave.getIdCity(), "idCity", true)))
        .build();
  }

  /**
   * Преобразует модель таблицы "user" в объект информации о пользователе.
   * В преобразовании не участвуют поля с датами рождениями детей и датой рождения пользователя
   * @param userEntity модель таблицы "user"
   * @param childBirthEntityList список моделей таблицы "child", связанных с пользователем
   * @param roleEntityList список моделей таблицы "role", связанных с пользователем
   * @param nameCity название города пользователя
   * @return информация о пользователе
   */
  public static UserInfo toInfo(UserEntity userEntity, List<ChildBirthEntity> childBirthEntityList, List<RoleEntity> roleEntityList, String nameCity) {

    if (userEntity == null) {
      return new UserInfo();
    }

    return UserInfo
        .builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .email(userEntity.getEmail())
        .birthDateChildren(childBirthEntityList
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

