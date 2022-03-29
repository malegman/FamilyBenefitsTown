package com.example.familybenefitstown.part_auth.filter.request_handlers;

import com.example.familybenefitstown.part_auth.models.AuthenticateResponse;
import com.example.familybenefitstown.part_auth.models.JwtUserData;
import com.example.familybenefitstown.part_auth.models.RequestHandlerResponse;
import com.example.familybenefitstown.part_auth.services.interfaces.AuthService;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.resources.RDB;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Обрабатывает запросы вида "/api/cities" на основе их данных аутентификации и авторизации.
 */
@Component
public class CityRequestHandler {

  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/api/cities/(id)"
   */
  private static final Pattern PATTERN_CITIES_ID = Pattern.compile(String.format(
      "^/api/cities/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));

  /**
   * Интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  private final AuthService authService;

  /**
   * Конструктор для инициализации сервисов
   * @param authService интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  public CityRequestHandler(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Обрабатывает http запрос и изменяет http ответ. Ответ может быть изменен в следующих случаях:
   * <ol>
   *   <li>Запрос не прошел проверку на аутентификацию и авторизацию. В ответ записывается 401 или 403 код статуса.</li>
   *   <li>Запрос на вход или выход. Необходимо установить или удалить токены.</li>
   *   <li>Запрос содержит просроченный токен доступа. В ответ записываются обновленные токены.</li>
   *   <li>Запрос содержит просроченный токен восстановления или невалидные токены. Из ответа удаляются токены.</li>
   *   <li>API не поддерживает конечную точку, указанную в запросе. В ответ записывается 405 код статуса.</li>
   * </ol>
   * @param request http запрос
   * @param response http ответ
   * @return объект, содержащий флаг успешности проверки и ответ
   */
  public RequestHandlerResponse handle(HttpServletRequest request, HttpServletResponse response) {

    String requestURI = request.getRequestURI();
    String requestMethod = request.getMethod();

    Matcher matcherCitiesId = PATTERN_CITIES_ID.matcher(requestURI);

    // Разрешение запросов, которые доступны всем
    if (requestMethod.equals("GET") &&
        (requestURI.equals("/api/cities") || matcherCitiesId.matches())) {
      return new RequestHandlerResponse(true, response);
    }

    // Проверка аутентификации и авторизации для запросов, предназначенных для авторизованных пользователей
    if (((requestMethod.equals("PUT") || requestMethod.equals("DELETE")) &&
        matcherCitiesId.matches())
        ||
        (requestMethod.equals("POST") && (requestURI.equals("/api/cities")))) {

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      Optional<AuthenticateResponse> optAuthenticateResponse = authService.authenticate(request, response);
      if (optAuthenticateResponse.isEmpty()) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new RequestHandlerResponse(false, response);
      }
      JwtUserData userData = optAuthenticateResponse.get().getUserData();

      // Проверка авторизации по наличию необходимых ролей
      if (!userData.hasRole(List.of(RDB.ROLE_ADMIN))) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return new RequestHandlerResponse(false, response);
      }
      return new RequestHandlerResponse(true, response);
    }

    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    return new RequestHandlerResponse(false, response);
  }
}
