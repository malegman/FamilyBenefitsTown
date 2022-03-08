package com.example.familybenefitstown.exceptions;

/**
 * Исключение, связанное с пользовательскими ролями
 */
public class UserRoleException extends Exception {

  /**
   * Конструктор, создает исключение с описанием исключения
   * @param message описание исключения
   */
  public UserRoleException(String message) {
    super(message);
  }
}
