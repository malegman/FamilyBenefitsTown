package com.example.familybenefitstown.part_auth.models;

import com.example.familybenefitstown.part_auth.services.interfaces.AuthService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * Объект ответа сервиса {@link AuthService} метода {@code authenticate}.
 * Содержит http ответ {@link HttpServletResponse} и данные пользователя {@link JwtUserData}.
 */
@Getter
@Builder
@AllArgsConstructor
public class AuthenticateResponse {

  /**
   * Http ответ
   */
  private HttpServletResponse response;

  /**
   * Данные пользователя
   */
  private JwtUserData userData;
}
