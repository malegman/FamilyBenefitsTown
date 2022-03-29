package com.example.familybenefitstown.part_auth.models;

import com.example.familybenefitstown.part_auth.services.interfaces.TokenCodeService;
import lombok.Builder;
import lombok.Getter;

/**
 * Контейнер, содержащий данные токены доступа (jwt) и восстановления. Создается сервисом {@link TokenCodeService}
 */
@Builder
@Getter
public class AuthData {

  /**
   * Данные токена доступа jwt
   */
  private JwtData jwtData;

  /**
   * Токен восстановления
   */
  private String refreshToken;
}
