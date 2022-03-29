package com.example.familybenefitstown.part_auth.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * Объект ответа сервиса обработки запросов на аутентификацию и авторизацию.
 * Содержит в себе результат проверки и http ответ обработанного запроса.
 */
@Getter
@AllArgsConstructor
public class RequestHandlerResponse {

  /**
   * Результат проверки. {@code true}, если проверка пройдена успешно
   */
  private boolean isSuccess;
  /**
   * Ответ проверяемого запроса
   */
  private HttpServletResponse response;
}
