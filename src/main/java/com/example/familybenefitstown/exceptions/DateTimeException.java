package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное с ошибками со временем
 */
public class DateTimeException extends Exception {

  /**
   * Код варианта ошибки в api
   */
  public static final int API_VARIANT_CODE = 3;

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public DateTimeException(String message) {
    super(message);
  }
}
