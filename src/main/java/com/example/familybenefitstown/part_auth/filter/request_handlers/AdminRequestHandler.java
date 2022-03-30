package com.example.familybenefitstown.part_auth.filter.request_handlers;

import com.example.familybenefitstown.part_auth.models.JwtUserData;
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
 * Обрабатывает запросы вида "/api/admins" на основе их данных аутентификации и авторизации.
 */
@Component
public class AdminRequestHandler {

  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/api/admins/(id)"
   */
  private static final Pattern PATTERN_ADMINS_ID = Pattern.compile(String.format(
      "^/api/admins/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));

  /**
   * Интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  private final AuthService authService;

  /**
   * Конструктор для инициализации сервисов
   * @param authService интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  public AdminRequestHandler(AuthService authService) {
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
   * @return true, если запрос успешно обработан
   */
  public boolean handle(HttpServletRequest request, HttpServletResponse response) {

    String requestURI = request.getRequestURI();
    String requestMethod = request.getMethod();

    Matcher matcherAdminsId = PATTERN_ADMINS_ID.matcher(requestURI);

    // Проверка аутентификации и авторизации для запросов, которые для авторизованных пользователей
    if ((requestMethod.equals("GET") || requestMethod.equals("PUT")) &&
        matcherAdminsId.matches()) {

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      Optional<JwtUserData> optUserData = authService.authenticate(request, response);
      if (optUserData.isEmpty()) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
      }
      JwtUserData userData = optUserData.get();

      // Проверка авторизации по наличию необходимых ролей и ID
      if (!userData.hasRole(List.of(RDB.ROLE_ADMIN)) ||
          !userData.getIdUser().equals(matcherAdminsId.group("id"))) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
      }
      return true;
    }

    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    return false;
  }
}
