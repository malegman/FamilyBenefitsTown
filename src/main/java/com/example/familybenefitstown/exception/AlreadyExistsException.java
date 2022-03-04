package com.example.familybenefitstown.exception;

/**
 * Исключение, связанное с существованием объекта в базе данных
 */
public class AlreadyExistsException extends Exception {

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public AlreadyExistsException(String message) {
    super(message);
  }
}
