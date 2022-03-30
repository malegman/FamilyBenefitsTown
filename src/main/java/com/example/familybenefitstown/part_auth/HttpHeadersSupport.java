package com.example.familybenefitstown.part_auth;

import com.example.familybenefitstown.part_auth.models.AuthData;
import com.example.familybenefitstown.resources.R;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Предоставляет статические методы для обработки http заголовков, работы с токенами безопасности
 */
public class HttpHeadersSupport {

  /**
   * Удаляет токен восстановления из Cookie refresh в http ответе
   * @param response http ответ, из которого необходимо удалить Cookie refresh
   */
  public static void removeRefreshToken(HttpServletResponse response) {

    Cookie cookie = new Cookie(R.REFRESH_NAME_COOKIE, "");
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
  }

  /**
   * Устанавливает в http ответ токен доступа в заголовок Authorization и токен восстановления в Cookie refresh
   * @param response http ответ, в который необходимо установить токены
   * @param authData объект, содержащий токены
   */
  public static void setTokens(HttpServletResponse response, AuthData authData) {

    response.addHeader(R.AUTHORIZATION_HEADER,
                       String.format(R.AUTHORIZATION_VALUE_PATTERN, authData.getJwtData().getTokenJwt()));
    Cookie cookie = new Cookie(R.REFRESH_NAME_COOKIE, authData.getRefreshToken());
    cookie.setMaxAge((int) R.REFRESH_EXPIRATION_SEC);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
  }

  /**
   * Извлекает токен доступа jwt из http запроса. {@code null}, если токен не найден
   * @param request http запрос
   * @return токен доступа пользователя
   */
  public static String getJwt(HttpServletRequest request) {

    if (request == null) {
      return null;
    }

    String bearer = request.getHeader(R.AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearer) && bearer.startsWith(R.ACCESS_TOKEN_PREFIX)) {
      return bearer.substring(R.ACCESS_TOKEN_PREFIX.length());
    }

    return null;
  }

  /**
   * Извлекает токен восстановления из http запроса. {@code null}, если токен не найден
   * @param request http запрос
   * @return токен восстановления пользователя
   */
  public static String getRefreshToken(HttpServletRequest request) {

    if (request == null) {
      return null;
    }

    for (Cookie cookie : request.getCookies()) {
      if (cookie.getName().equals(R.REFRESH_NAME_COOKIE)) {
        return cookie.getValue();
      }
    }

    return null;
  }
}
