package com.example.familybenefitstown.security;

import com.example.familybenefitstown.exceptions.InvalidEmailException;
import com.example.familybenefitstown.resources.RMail;

/**
 * Предоставляет статические методы для безопасной работы с email
 */
public class MailSecuritySupport {

  /**
   * Проверяет корректность email
   * @param email проверяемый email
   * @throws InvalidEmailException если указанный "email" не является email
   */
  public static void checkEmailElseThrow(String email) throws InvalidEmailException {

    if (email == null || !RMail.PATTERN_EMAIL.matcher(email).matches()) {
      throw new InvalidEmailException(String.format("Input value \"%s\" is not an email", email));
    }
  }
}
