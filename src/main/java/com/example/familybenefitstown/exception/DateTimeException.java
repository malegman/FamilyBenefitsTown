package com.example.familybenefitstown.exception;

/**
 * Исключение, связанное с ошибками со временем
 */
public class DateTimeException extends Exception {

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public DateTimeException(String message) {
    super(message);
  }
}
