package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное со строковым параметром из объекта запроса
 */
public class InvalidStringException extends Exception {

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public InvalidStringException(String message) {
    super(message);
  }
}
