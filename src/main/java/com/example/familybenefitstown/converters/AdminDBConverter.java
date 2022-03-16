package com.example.familybenefitstown.converters;

import com.example.familybenefitstown.api_models.admin.AdminInfo;
import com.example.familybenefitstown.api_models.admin.AdminSave;
import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.dto.entities.UserEntity;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.resources.R;

import java.util.List;
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
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  static public UserEntity fromSave(AdminSave adminSave, Function<String, String> prepareDBFunc) throws InvalidStringException {

    if (adminSave == null) {
      return new UserEntity();
    }

    return UserEntity
        .builder()
        .name(prepareDBFunc.apply(withSymbolsField(adminSave.getName(), "name", true)))
        .email(prepareDBFunc.apply(withSymbolsField(adminSave.getEmail(), "email", true)))
        .build();
  }

  /**
   * Преобразует модель таблицы "user" в объект информации об администраторе
   * @param userEntity модель таблицы "user"
   * @param roleEntityList список моделей таблицы "role", связанных с администратором
   * @return информация об администраторе
   */
  static public AdminInfo toInfo(UserEntity userEntity, List<RoleEntity> roleEntityList) {

    if (userEntity == null) {
      return new AdminInfo();
    }

    return AdminInfo
        .builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .email(userEntity.getEmail())
        .nameRoleList(roleEntityList
                         .stream()
                         .map(RoleEntity::getName)
                         .collect(Collectors.toList()))
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
