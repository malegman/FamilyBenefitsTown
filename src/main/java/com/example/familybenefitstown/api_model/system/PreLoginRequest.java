package com.example.familybenefitstown.api_model.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект запроса пользователя для получения кода для входа в систему
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreLoginRequest {

  /**
   * Электронная почта пользователя
   */
  @JsonProperty("email")
  private String email;
}
