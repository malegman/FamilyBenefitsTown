package com.example.familybenefitstown.part_auth.models;

import lombok.Builder;
import lombok.Getter;

/**
 * Контейнер, содержащий токен доступа формата jwt и его полезную нагрузку, данные пользователя {@link JwtUserData}.
 */
@Builder
@Getter
public class JwtData {

  /**
   * Токен доступа в формате jwt
   */
  private String tokenJwt;

  /**
   * Данные пользователя
   */
  private JwtUserData userData;
}
