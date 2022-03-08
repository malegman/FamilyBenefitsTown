package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное с некорректным форматом email
 */
public class InvalidEmailException extends Exception {

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public InvalidEmailException(String message) {
    super(message);
  }
}
