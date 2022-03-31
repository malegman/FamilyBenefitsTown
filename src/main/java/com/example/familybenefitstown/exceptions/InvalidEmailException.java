package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное с некорректным форматом email
 */
public class InvalidEmailException extends Exception {

  /**
   * Код варианта ошибки в api
   */
  public static final int API_VARIANT_CODE = 4;

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public InvalidEmailException(String message) {
    super(message);
  }
}
