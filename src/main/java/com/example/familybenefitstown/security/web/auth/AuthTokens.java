package com.example.familybenefitstown.security.web.auth;

import com.example.familybenefitstown.security.services.interfaces.TokenCodeService;
import lombok.Builder;
import lombok.Getter;

/**
 * Контейнер, содержащий токены доступа (jwt) и восстановления. Создается сервисом {@link TokenCodeService}
 */
@Builder
@Getter
public class AuthTokens {

  /**
   * Токен доступа в формате jwt
   */
  private String jwt;

  /**
   * Токен восстановления
   */
  private String refreshToken;
}
