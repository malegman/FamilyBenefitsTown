package com.example.familybenefitstown.exception;

/**
 * Исключение, связанное с некорректным форматом даты
 */
public class DateFormatException extends Exception {

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public DateFormatException(String message) {
    super(message);
  }
}
