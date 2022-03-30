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
 * Обрабатывает запросы вида "/api/sa" на основе их данных аутентификации и авторизации.
 */
@Component
public class SuperAdminRequestHandler {

  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/api/sa/admins/(id)"
   */
  private static final Pattern PATTERN_SA_ADMINS_ID = Pattern.compile(String.format(
      "^/api/sa/admins/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/api/sa/from-user/(id)"
   */
  private static final Pattern PATTERN_SA_FROM_USER_ID = Pattern.compile(String.format(
      "^/api/sa/from-user/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/api/sa/to-user/(id)"
   */
  private static final Pattern PATTERN_SA_TO_USER_ID = Pattern.compile(String.format(
      "^/api/sa/to-user/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/api/sa/to-super/(id)"
   */
  private static final Pattern PATTERN_SA_TO_SUPER_ID = Pattern.compile(String.format(
      "^/api/sa/to-super/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));

  /**
   * Интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  private final AuthService authService;

  /**
   * Конструктор для инициализации сервисов
   * @param authService интерфейс сервиса, отвечающего за аутентификацию и авторизацию в системе
   */
  public SuperAdminRequestHandler(AuthService authService) {
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

    Matcher matcherSaAdminsId = PATTERN_SA_ADMINS_ID.matcher(requestURI);
    Matcher matcherSaFromUserId = PATTERN_SA_FROM_USER_ID.matcher(requestURI);
    Matcher matcherSaToSuperId = PATTERN_SA_TO_SUPER_ID.matcher(requestURI);
    Matcher matcherSaToUserId = PATTERN_SA_TO_USER_ID.matcher(requestURI);
    boolean isSaAdminsId = false;
    boolean isSaFromUserId = false;
    boolean isSaToSuperId = false;
    boolean isSaToUserId = false;

    String requestId = null;

    // Проверка аутентификации и авторизации для запросов, которые для авторизованных пользователей
    if (requestMethod.equals("POST") &&
        (requestURI.equals("/api/sa/admins") || (isSaToUserId = matcherSaToUserId.matches()) || (isSaFromUserId = matcherSaFromUserId.matches()))
        ||
        requestMethod.equals("DELETE") && (isSaAdminsId = matcherSaAdminsId.matches())
        ||
        requestMethod.equals("POST") && (isSaToSuperId = matcherSaToSuperId.matches())) {

      if (isSaToUserId) {
        requestId = matcherSaToUserId.group("id");
      } else if (isSaFromUserId) {
        requestId = matcherSaFromUserId.group("id");
      } else if (isSaAdminsId) {
        requestId = matcherSaAdminsId.group("id");
      } else if (isSaToSuperId) {
        requestId = matcherSaToSuperId.group("id");
      }

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      Optional<JwtUserData> optUserData = authService.authenticate(request, response);
      if (optUserData.isEmpty()) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
      }
      JwtUserData userData = optUserData.get();

      // Проверка авторизации по наличию необходимых ролей и ID
      if (!userData.hasRole(List.of(RDB.ROLE_SUPER_ADMIN)) ||
          (!isSaAdminsId && !userData.getIdUser().equals(requestId)) ||
          (isSaAdminsId && userData.getIdUser().equals(requestId))) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
      }
      return true;
    }

    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    return false;
  }
}
