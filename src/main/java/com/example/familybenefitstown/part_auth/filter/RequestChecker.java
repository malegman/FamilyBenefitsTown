package com.example.familybenefitstown.part_auth.filter;

import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.part_auth.models.RequestCheckResponse;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.resources.RDB;
import com.example.familybenefitstown.part_auth.services.interfaces.TokenCodeService;
import com.example.familybenefitstown.part_auth.models.AuthTokens;
import com.example.familybenefitstown.part_auth.models.JwtUserData;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервис для проверки запроса на аутентификацию и авторизацию
 */
@Slf4j
@Service
public class RequestChecker {

  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/cities/(id)"
   */
  private static final Pattern PATTERN_CITIES_ID = Pattern.compile(String.format(
      "^/cities/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/users/(id)"
   */
  private static final Pattern PATTERN_USERS_ID = Pattern.compile(String.format(
      "^/users/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/admins/(id)"
   */
  private static final Pattern PATTERN_ADMINS_ID = Pattern.compile(String.format(
      "^/admins/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/sa/admins/(id)"
   */
  private static final Pattern PATTERN_SA_ADMINS_ID = Pattern.compile(String.format(
      "^/sa/admins/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/sa/from-user/(id)"
   */
  private static final Pattern PATTERN_SA_FROM_USER_ID = Pattern.compile(String.format(
      "^/sa/from-user/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/sa/to-user/(id)"
   */
  private static final Pattern PATTERN_SA_TO_USER_ID = Pattern.compile(String.format(
      "^/sa/to-user/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));
  /**
   * Шаблон для проверки соответствия и извлечения параметра (id) из запроса "/sa/to-super/(id)"
   */
  private static final Pattern PATTERN_SA_TO_SUPER_ID = Pattern.compile(String.format(
      "^/sa/to-super/(?<id>[A-Za-z0-9]{%s})$", R.ID_LENGTH));

  /**
   * IP-адрес, с которого был выполнен запрос
   */
  private String requestAddress;
  /**
   * Метод запроса: "GET", "POST", "PUT", "DELETE"
   */
  private String requestMethod;
  /**
   * Путь запроса, конечная точка
   */
  private String requestURI;
  /**
   * Объект данных пользователя, который будет заполнен в случае успешной аутентификации запроса
   */
  private JwtUserData userData = null;
  /**
   * http ответ проверяемого запроса, после проверки
   */
  private HttpServletResponse responseAfterCheck;

  /**
   * Интерфейс сервиса для работы с токенами доступа (в формате jwt) и восстановления и кодом для входа
   */
  private final TokenCodeService tokenCodeService;
  /**
   * Интерфейс сервиса, управляющего объектом "пользователь"
   */
  private final UserService userService;

  /**
   * Конструктор для инициализации сервиса и репозитория
   * @param userService интерфейс сервиса, управляющего объектом "пользователь"
   * @param tokenCodeService интерфейс сервиса для работы с токеном доступа (в формате jwt) и кодом для входа
   */
  @Autowired
  public RequestChecker(UserService userService,
                        TokenCodeService tokenCodeService) {
    this.userService = userService;
    this.tokenCodeService = tokenCodeService;
  }

  /**
   * Инициализация проверки запроса.
   * Разбирает запрос http на поля об адресе, пути и типе метода. Устанавливает поле http ответа.
   * @param request проверяемый http запрос
   * @param response http ответ проверяемого запроса
   */
  private void initCheck(HttpServletRequest request, HttpServletResponse response) {

    requestURI = request.getRequestURI();
    requestMethod = request.getMethod();
    requestAddress = request.getRemoteAddr();
    responseAfterCheck = response;

    log.debug("{} {} \"{}\": Request in check", requestAddress, requestMethod, requestURI);
  }

  /**
   * Проверяет клиентский запрос, связанный с городом, на аутентификацию и авторизацию
   * @param request проверяемый http запрос
   * @param response http ответ проверяемого запроса
   * @return Объект с результатом проверки запроса и с измененным ответом
   */
  public RequestCheckResponse checkCity(HttpServletRequest request, HttpServletResponse response) {

    initCheck(request, response);

    Matcher matcherCitiesId = PATTERN_CITIES_ID.matcher(requestURI);

    // Разрешение запросов, которые доступны всем
    if (requestMethod.equals("GET") &&
        (requestURI.equals("/cities") || matcherCitiesId.matches())) {
      return new RequestCheckResponse(true, responseAfterCheck);
    }

    // Проверка аутентификации и авторизации для запросов, которые для авторизованных пользователей
    if (((requestMethod.equals("PUT") || requestMethod.equals("DELETE")) &&
        matcherCitiesId.matches())
        ||
        (requestMethod.equals("POST") && (requestURI.equals("/cities")))) {

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      if (notAuthenticated(request)) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      // Проверка авторизации по наличию необходимых ролей
      if (notHasRole(userData.getNameRoleSet(), Collections.singleton(RDB.NAME_ROLE_ADMIN))) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      return new RequestCheckResponse(true, responseAfterCheck);
    }

    responseAfterCheck.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    return new RequestCheckResponse(false, responseAfterCheck);
  }

  /**
   * Проверяет клиентский запрос, связанный с пользователем, на аутентификацию и авторизацию
   * @param request проверяемый http запрос
   * @param response http ответ проверяемого запроса
   * @return Объект с результатом проверки запроса и с измененным ответом
   */
  public RequestCheckResponse checkUser(HttpServletRequest request, HttpServletResponse response) {

    initCheck(request, response);

    // Разрешение запросов, которые доступны всем
    if (requestMethod.equals("GET") && requestURI.equals("/users/init-data")) {
      return new RequestCheckResponse(true, responseAfterCheck);
    }

    // Разрешение запросов для анонимных пользователей
    if (requestMethod.equals("POST") && requestURI.equals("/users")) {

      // Проверка отсутствия аутентификации по наличию токена восстановления
      if (tokenCodeService.refreshTokenFromRequest(request) == null) {
        return new RequestCheckResponse(true, responseAfterCheck);
      }
      responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return new RequestCheckResponse(false, responseAfterCheck);
    }

    Matcher matcherUsersId = PATTERN_USERS_ID.matcher(requestURI);

    // Проверка аутентификации и авторизации для запросов, которые для авторизованных пользователей
    if ((requestMethod.equals("GET") || requestMethod.equals("PUT") || requestMethod.equals("DELETE")) &&
        matcherUsersId.matches()) {

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      if (notAuthenticated(request)) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      // Проверка авторизации по наличию необходимых ролей и соответствию ID пользователя ID ресурса
      if (notHasRole(userData.getNameRoleSet(), Collections.singleton(RDB.NAME_ROLE_USER)) ||
          notEqualsId(userData.getIdUser(), matcherUsersId.group("id"))) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      return new RequestCheckResponse(true, responseAfterCheck);
    }

    responseAfterCheck.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    return new RequestCheckResponse(false, responseAfterCheck);
  }

  /**
   * Проверяет клиентский запрос, связанный с администратором, на аутентификацию и авторизацию
   * @param request проверяемый http запрос
   * @param response http ответ проверяемого запроса
   * @return Объект с результатом проверки запроса и с измененным ответом
   */
  public RequestCheckResponse checkAdmin(HttpServletRequest request, HttpServletResponse response) {

    initCheck(request, response);

    Matcher matcherAdminsId = PATTERN_ADMINS_ID.matcher(requestURI);

    // Проверка аутентификации и авторизации для запросов, которые для авторизованных пользователей
    if ((requestMethod.equals("GET") || requestMethod.equals("PUT")) &&
        matcherAdminsId.matches()) {

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      if (notAuthenticated(request)) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      // Проверка авторизации по наличию необходимых ролей и соответствию ID пользователя ID ресурса
      if (notHasRole(userData.getNameRoleSet(), Collections.singleton(RDB.NAME_ROLE_ADMIN)) ||
          notEqualsId(userData.getIdUser(), matcherAdminsId.group("id"))) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      return new RequestCheckResponse(true, responseAfterCheck);
    }

    responseAfterCheck.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    return new RequestCheckResponse(false, responseAfterCheck);
  }

  /**
   * Проверяет клиентский запрос, связанный с супер-администратором, на аутентификацию и авторизацию
   * @param request проверяемый http запрос
   * @param response http ответ проверяемого запроса
   * @return Объект с результатом проверки запроса и с измененным ответом
   */
  public RequestCheckResponse checkSuperAdmin(HttpServletRequest request, HttpServletResponse response) {

    initCheck(request, response);

    Matcher matcherSaAdminsId = PATTERN_SA_ADMINS_ID.matcher(requestURI);
    Matcher matcherSaFromUserId = PATTERN_SA_FROM_USER_ID.matcher(requestURI);
    Matcher matcherSaToSuperId = PATTERN_SA_TO_SUPER_ID.matcher(requestURI);
    Matcher matcherSaToUserId = PATTERN_SA_TO_USER_ID.matcher(requestURI);

    // Проверка аутентификации и авторизации для запросов, которые для авторизованных пользователей
    // Запросы на создание админа и добавление ролей
    if (requestMethod.equals("POST") &&
        (requestURI.equals("/sa/admins") || matcherSaToUserId.matches() || matcherSaFromUserId.matches())) {

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      if (notAuthenticated(request)) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      // Проверка авторизации по наличию необходимых ролей
      if (notHasRole(userData.getNameRoleSet(), Collections.singleton(RDB.NAME_ROLE_SUPER_ADMIN))) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      return new RequestCheckResponse(true, responseAfterCheck);
    }
    // Запрос на удаление администратора или передача супер-администратора
    boolean isSaAdminsId = requestMethod.equals("DELETE") && matcherSaAdminsId.matches();
    boolean isSaToSuperId = requestMethod.equals("POST") && matcherSaToSuperId.matches();
    if (isSaAdminsId || isSaToSuperId) {

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      if (notAuthenticated(request)) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      // Проверка авторизации по наличию необходимых ролей и не совпадению ID
      String requestIdUser = isSaAdminsId ? matcherSaAdminsId.group("id") : matcherSaToSuperId.group("id");
      if (notHasRole(userData.getNameRoleSet(), Collections.singleton(RDB.NAME_ROLE_SUPER_ADMIN)) ||
          !notEqualsId(userData.getIdUser(), requestIdUser)) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      return new RequestCheckResponse(true, responseAfterCheck);
    }

    responseAfterCheck.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    return new RequestCheckResponse(false, responseAfterCheck);
  }

  /**
   * Проверяет клиентский запрос, связанный с аутентификацией и авторизацией, на аутентификацию и авторизацию
   * @param request проверяемый http запрос
   * @param response http ответ проверяемого запроса
   * @return Объект с результатом проверки запроса и с измененным ответом
   */
  public RequestCheckResponse checkAuth(HttpServletRequest request, HttpServletResponse response) {

    initCheck(request, response);

    // Разрешение запросов для анонимных пользователей
    // Запрос на получение кода для входа
    if (requestMethod.equals("POST") && requestURI.equals("/auth/pre-login")) {

      // Проверка отсутствия аутентификации по наличию токена восстановления
      if (tokenCodeService.refreshTokenFromRequest(request) == null) {
        return new RequestCheckResponse(true, responseAfterCheck);
      }
      responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return new RequestCheckResponse(false, responseAfterCheck);
    }
    // Запрос на вход в систему
    if (requestMethod.equals("POST") && requestURI.equals("/auth/login")) {

      // Проверка отсутствия аутентификации по наличию токена восстановления
      if (tokenCodeService.refreshTokenFromRequest(request) == null) {
        // Генерация токенов доступа и восстановления
        if (setAuthTokensByLoginCodeToResponse(request.getParameter("e"),
                                               Integer.parseInt(request.getParameter("lc")))) {
          return new RequestCheckResponse(true, responseAfterCheck);
        }
        responseAfterCheck.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
      return new RequestCheckResponse(false, responseAfterCheck);
    }

    // Проверка аутентификации и авторизации для запросов, которые для авторизованных пользователей
    if (requestMethod.equals("POST") &&
        (requestURI.equals("/auth/logout"))) {

      // Проверка аутентификации по токенам доступа (jwt) и восстановления из запроса
      if (notAuthenticated(request)) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      // Проверка авторизации по наличию необходимых ролей
      if (notHasRole(userData.getNameRoleSet(), Set.of(RDB.NAME_ROLE_USER, RDB.NAME_ROLE_ADMIN))) {
        responseAfterCheck.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return new RequestCheckResponse(false, responseAfterCheck);
      }
      // Удаление cookie с токеном восстановления
      removeRefreshFromResponse(tokenCodeService.refreshTokenFromRequest(request));
      return new RequestCheckResponse(true, responseAfterCheck);
    }

    responseAfterCheck.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    return new RequestCheckResponse(false, responseAfterCheck);
  }

  /**
   * Проверяет запрос на аутентификацию.
   * При успешной аутентификации, при возврате {@code false}, заполняет параметр {@code userData} данными пользователя.
   * Если токен восстановления корректный и jwt валидный, но истекший, заполняется параметр {@code newAuthTokens} новыми токенами.
   * Если токен восстановления корректный, а jwt некорректный, то токен восстановления удаляется из бд.
   * Если параметр для заполнения не заполняется, то он устанавливается в {@code null}
   * @param request проверяемый http запрос
   * @return {@code true}, если запрос не аутентифицирован
   */
  private boolean notAuthenticated(HttpServletRequest request) {

    String requestJwt = tokenCodeService.jwtFromRequest(request);
    String requestRefreshToken = tokenCodeService.refreshTokenFromRequest(request);

    // Проверка токена восстановления
    String idUser;
    try {
      idUser = tokenCodeService.getIdOfNotExpiredRefreshToken(requestRefreshToken);
    } catch (DateTimeException | NotFoundException e) {
      log.warn("{} {} \"{}\": Refresh token's exceptions. {}", requestAddress, requestMethod, requestURI, e.getMessage());
      return true;
    }

    AuthTokens newAuthTokens;

    // Проверка токена jwt
    try {
      userData = tokenCodeService.dataFromJwt(requestJwt);

    } catch (ExpiredJwtException e) {
      // Токен jwt истек, но корректный.
      // Запрос новых токенов и сохранение в бд токена восстановления
      try {
        newAuthTokens = tokenCodeService.generateAuthTokens(idUser);
      } catch (NotFoundException ex) {
        // Не найден пользователь
        log.warn("{} {} \"{}\": Authentication exceptions. {}", requestAddress, requestMethod, requestURI, ex.getMessage());
        return true;
      }
      // Установка токенов в заголовки http ответа
      setTokensToResponse(newAuthTokens);

    } catch (RuntimeException e) {
      // Токен jwt некорректный.
      // Удаление токена восстановления.
      tokenCodeService.removeRefreshToken(requestRefreshToken);
      log.warn("{} {} \"{}\": Incorrect jwt \"{}\" from the user with id \"{}\". {}",
               requestAddress, requestMethod, requestURI, requestJwt, idUser, e.getMessage());
      return true;
    }

    return false;
  }

  /**
   * Генерирует новые токены доступа и восстановления по коду для входа пользователя.
   * Устанавливает токены в заголовки ответа.
   * Если код доступа не найден или истек, или не найден пользователь по email, то возвращает {@code false}
   * @param email почта пользователя
   * @param loginCode пользовательский код для входа
   * @return {@code true}, если токены получены и установлены в ответ
   */
  private boolean setAuthTokensByLoginCodeToResponse(String email, int loginCode) {

    // Проверка существования пользователя по email
    if (!userService.existsByEmail(email)) {
      log.warn("{} {} \"{}\": User with email \"{}\" not found.", requestAddress, requestMethod, requestURI, email);
      return false;
    }

    AuthTokens newAuthTokens;

    // Генерация токенов по коду
    try {
      newAuthTokens = tokenCodeService.generateAuthTokens(loginCode);
    } catch (NotFoundException | DateTimeException e) {
      log.warn("{} {} \"{}\": Login code exceptions. {}", requestAddress, requestMethod, requestURI, e);
      return false;
    }

    // Установка токенов в заголовки http ответа
    setTokensToResponse(newAuthTokens);

    return true;
  }

  /**
   * Устанавливает токены в заголовки http ответа проверяемого запроса
   * @param authTokens объект с токенами доступа (jwt) восстановления
   */
  private void setTokensToResponse(AuthTokens authTokens) {

    responseAfterCheck.addHeader(R.AUTHORIZATION_HEADER,
                                 String.format(R.AUTHORIZATION_VALUE_PATTERN, authTokens.getJwt()));
    Cookie cookie = new Cookie(R.REFRESH_NAME_COOKIE, authTokens.getRefreshToken());
    cookie.setMaxAge((int) R.REFRESH_EXPIRATION_SEC);
    cookie.setHttpOnly(true);
    responseAfterCheck.addCookie(cookie);
  }

  /**
   * Удаляет cookie с токеном восстановления из ответа
   * @param refreshToken токен восстановления, который необходимо удалить
   */
  private void removeRefreshFromResponse(String refreshToken) {

    Cookie cookie = new Cookie(R.REFRESH_NAME_COOKIE, refreshToken);
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);
    responseAfterCheck.addCookie(cookie);
  }

  /**
   * Проверяет отсутствие у пользователя одной из указанной роли
   * @param userNameRoleSet множество ролей пользователя
   * @param requestNameRoleSet множество ролей, наличие одной из которых позволяет выполнить запрос
   * @return {@code true}, если пользователь не имеет одну из указанных ролей
   */
  private boolean notHasRole(Set<String> userNameRoleSet, Set<String> requestNameRoleSet) {

    boolean hasRole = false;
    for (String requestNameRole : requestNameRoleSet) {
      if (userNameRoleSet.contains(requestNameRole)) {
        hasRole = true;
        break;
      }
    }
    if (!hasRole) {
      log.warn("{} {} \"{}\": The user doesn't have any of the role: {}.",
               requestAddress, requestMethod, requestURI, String.join(", ", requestNameRoleSet));
      return true;
    }

    return false;
  }

  /**
   * Проверяет на отличие ID пользователя от ID запрашиваемого ресурса.
   * Проверку следует выполнять, если запрашивается ресурс о пользователе.
   * @param userId ID пользователя
   * @param resourceId ID запрашиваемого ресурса
   * @return {@code true}, если ID не совпадают
   */
  private boolean notEqualsId(String userId, String resourceId) {

    if (!resourceId.equals(userId)) {
      log.warn("{} {} \"{}\": The user with id \"{}\" tried to get information about user's resources with id \"{}\".",
               requestAddress, requestMethod, requestURI, userId, resourceId);
      return true;
    }

    return false;
  }
}
