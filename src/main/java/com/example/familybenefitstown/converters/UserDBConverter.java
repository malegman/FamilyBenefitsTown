package com.example.familybenefitstown.converters;

import com.example.familybenefitstown.api_models.user.UserInfo;
import com.example.familybenefitstown.api_models.user.UserSave;
import com.example.familybenefitstown.dto.entities.ChildBirthEntity;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.exceptions.InvalidStringException;
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
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  static public UserEntity fromSave(UserSave userSave, Function<String, String> prepareDBFunc) throws InvalidStringException {

    if (userSave == null) {
      return new UserEntity();
    }

    return UserEntity
        .builder()
        .name(prepareDBFunc.apply(withSymbolsField(userSave.getName(), "name", true)))
        .email(prepareDBFunc.apply(withSymbolsField(userSave.getEmail(), "email", true)))
        .idCity(prepareDBFunc.apply(withSymbolsField(userSave.getIdCity(), "idCity", true)))
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
  static public UserInfo toInfo(UserEntity userEntity, List<ChildBirthEntity> childBirthEntityList, List<RoleEntity> roleEntityList, String nameCity) {

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

  /**
   * Проверяет строковое поле на содержание букв латиницы и кириллицы и цифр.
   * При успешной проверки возвращается проверяемая строка без изменений.
   * Иначе выбрасывается исключение.
   * @param str проверяемая строка
   * @param field поле, значение которого проверяется
   * @param isRequired true, если поле является обязательным, не может быть null
   * @return проверяемая строка при успешной проверке
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  private static String withSymbolsField(String str, String field, boolean isRequired) throws InvalidStringException {

    if (str == null && !isRequired) {
      return null;
    }
    if (str != null && R.STRING_SYMBOLS_PATTERN.matcher(str).matches()) {
      return str;
    }

    throw new InvalidStringException(String.format(
        "Attempt to store a string without letters and numbers in the \"%s\" field ", field));
  }
}

