package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное с отсутствием объекта в базе данных
 */
public class NotFoundException extends Exception {

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public NotFoundException(String message) {
    super(message);
  }
}
