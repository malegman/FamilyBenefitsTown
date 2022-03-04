package com.example.familybenefitstown.api_model.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigInteger;

/**
 * Объект запроса пользователя для входа в систему
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  /**
   * Электронная почта пользователя
   */
  @JsonProperty("email")
  private String email;

  /**
   * Код пользователя для входа в систему
   */
  @JsonProperty("loginCode")
  private BigInteger loginCode;
}
