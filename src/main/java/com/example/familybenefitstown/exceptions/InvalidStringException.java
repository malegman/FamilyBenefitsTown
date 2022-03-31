package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное со строковым параметром из объекта запроса
 */
public class InvalidStringException extends Exception {

  /**
   * Код варианта ошибки в api
   */
  public static final int API_VARIANT_CODE = 5;

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public InvalidStringException(String message) {
    super(message);
  }
}
