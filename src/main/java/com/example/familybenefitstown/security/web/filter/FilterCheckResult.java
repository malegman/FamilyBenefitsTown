package com.example.familybenefitstown.security.web.filter;

/**
 * Предоставляет ответы проверок запроса внутри фильтра
 */
public enum FilterCheckResult {

  /**
   * Проверка пройдена успешно
   */
  SUCCESS,
  /**
   * Проверка провалена, клиент не аутентифицирован
   */
  FAIL_UNAUTHORIZED,
  /**
   * Проверка провалена, клиенту отказано в доступе
   */
  FAIL_FORBIDDEN,
  /**
   * Проверка провалена, метод не поддерживается
   */
  FAIL_METHOD_NOT_ALLOWED
}
