package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное с существованием объекта в базе данных
 */
public class AlreadyExistsException extends Exception {

  /**
   * Код варианта ошибки в api
   */
  public static final int API_VARIANT_CODE = 1;

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public AlreadyExistsException(String message) {
    super(message);
  }
}
