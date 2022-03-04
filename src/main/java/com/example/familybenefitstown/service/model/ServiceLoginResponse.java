package com.example.familybenefitstown.service.model;

import com.example.familybenefitstown.api_model.system.LoginResponse;
import lombok.Builder;
import lombok.Getter;

/**
 * Объект ответа сервиса "SystemService" на запрос входа в систему
 */
@Builder
@Getter
public class ServiceLoginResponse {

  /**
   * Токен доступа в формате jwt
   */
  private String jwt;

  /**
   * Ответ клиенту на вход в систему
   */
  private LoginResponse loginResponse;
}
