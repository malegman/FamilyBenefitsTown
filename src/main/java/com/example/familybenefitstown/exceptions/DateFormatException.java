package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное с некорректным форматом даты
 */
public class DateFormatException extends Exception {

  /**
   * Код варианта ошибки в api
   */
  public static final int API_VARIANT_CODE = 2;

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public DateFormatException(String message) {
    super(message);
  }
}
