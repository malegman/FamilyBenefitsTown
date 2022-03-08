package com.example.familybenefitstown.security.web.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * Объект ответа сервиса проверки запросов на аутентификацию и авторизацию.
 * Содержит в себе результат проверки и http ответ проверяемого запроса.
 */
@Getter
@AllArgsConstructor
public class RequestCheckResponse {

  /**
   * Результат проверки. {@code true}, если проверка пройдена успешно
   */
  private boolean isSuccess;
  /**
   * Ответ проверяемого запроса
   */
  private HttpServletResponse response;
}
