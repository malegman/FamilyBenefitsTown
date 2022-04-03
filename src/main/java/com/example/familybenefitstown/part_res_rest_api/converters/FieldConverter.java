package com.example.familybenefitstown.part_res_rest_api.converters;

import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.resources.R;

/**
 * Предоставляет статический метод конвертации строкового поля
 */
public class FieldConverter {

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
  public static String withSymbolsField(String str, String field, boolean isRequired) throws InvalidStringException {

    if (str == null && !isRequired) {
      return null;
    }
    if (str != null && R.STRING_SYMBOLS_PATTERN.matcher(str).find()) {
      return str;
    }

    throw new InvalidStringException(String.format(
        "Attempt to store a string without letters and numbers in the \"%s\" field ", field));
  }
}
