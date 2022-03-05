package com.example.familybenefitstown.security.web.filter;

import com.example.familybenefitstown.resource.R;
import com.example.familybenefitstown.security.service.inface.TokenCodeService;
import com.example.familybenefitstown.security.web.auth.JwtUserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Фильтр всех входящих http запросов
 */
@Slf4j
@Component
public class FilterFB extends GenericFilterBean {

  private static final Pattern PATTERN_CITIES_ID = Pattern.compile("^/cities/.+$");

  /**
   * Сервис для работы с токеном доступа (в формате jwt) и кодом для входа
   */
  private final TokenCodeService tokenCodeService;

  /**
   * Конструктор для инициализации сервиса модели пользователя
   * @param tokenCodeService сервис для работы с токеном доступа (в формате jwt) и кодом для входа
   */
  @Autowired
  public FilterFB(TokenCodeService tokenCodeService) {
    this.tokenCodeService = tokenCodeService;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String requestURI = request.getRequestURI();
    String requestMethod = request.getMethod();
    String requestAddress = request.getRemoteAddr();
    String jwt = jwtFromRequest(request);

    FilterCheckResult result;

    if (requestURI.startsWith("/cities")) {
      result = filterCheckCity(requestAddress, requestMethod, requestURI, jwt);
    } else if (requestURI.startsWith("/users")) {
      result = filterCheckUser(requestAddress, requestMethod, requestURI, jwt);
    } else if (requestURI.startsWith("/admins")) {
      result = filterCheckAdmin(requestAddress, requestMethod, requestURI, jwt);
    } else {
      result = filterCheckSystem(requestAddress, requestMethod, requestURI, jwt);
    }

    switch (result) {
      case SUCCESS -> filterChain.doFilter(request, response);
      case FAIL_UNAUTHORIZED -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      case FAIL_FORBIDDEN -> response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      case FAIL_METHOD_NOT_ALLOWED -> response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
  }

  /**
   * Извлекает токен из запроса клиента. null, если токен не найден
   * @param request запрос клиента
   * @return токен доступа пользователя.
   */
  private String jwtFromRequest(HttpServletRequest request) {

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
   * Извлекает данные пользователя из строки формата jwt. null, если извлечь не удалось.
   * @param jwt строка формата jwt
   * @param requestAddress адрес запроса, для лога
   * @param requestMethod тип метода запроса, для лога
   * @param requestURI путь запроса, для лога
   * @return данные пользователя или null, если извлечь не удалось
   */
  private JwtUserData userDataFromJwt(String jwt,
                                      String requestAddress, String requestMethod, String requestURI) {

    JwtUserData userData = null;
    try {
      userData = tokenCodeService.authFromStringJwt(jwt);
    } catch (RuntimeException e) {
      log.warn("{} {} \"{}\": Couldn't get user's data from jwt {}. {}",
               requestAddress, requestMethod, requestURI, jwt, e);
    }

    return userData;
  }

  private boolean isAuthorized(String jwt, String nameRole,
                               String requestAddress, String requestMethod, String requestURI) {

    if (jwt == null) {
      return false;
    }

    JwtUserData userData = userDataFromJwt(jwt, requestAddress, requestMethod, requestURI);
    if (userData == null) {
      return false;
    }

    return isAuthorized(userData, nameRole, userData.getAddress(),
                        requestAddress, requestMethod, requestURI);
  }

  private boolean isAuthorized(JwtUserData userData, String nameRole, String address,
                               String requestAddress, String requestMethod, String requestURI) {

    if (!userData.getNameRoleSet().contains(nameRole)) {
      log.warn("{} {} \"{}\": Client hasn't got the role \"{}\".",
               requestAddress, requestMethod, requestURI, nameRole);
      return false;
    }

    if (!userData.getAddress().equals(address)) {
      log.warn("{} {} \"{}\": Client sent request from another address \"{}\".",
               requestAddress, requestMethod, requestURI, address);
      return false;
    }

    return true;
  }

  private FilterCheckResult filterCheckCity(String requestAddress, String requestMethod, String requestURI, String jwt) {

    // Разрешение запросов, которые не требуют авторизации
    if (requestMethod.equals("GET") &&
        (requestURI.equals("/cities") || requestURI.equals("/cities/{id}"))) {

      return FilterCheckResult.SUCCESS;
    }

    if (PATTERN_CITIES_ID.matcher(requestURI).matches() &&
        (requestMethod.equals("POST") || requestMethod.equals("PUT") || requestMethod.equals("DELETE"))) {

      if (isAuthorized(jwt, R.ROLE_ADMIN, requestAddress, requestMethod, requestURI)) {
        return FilterCheckResult.SUCCESS;
      }

      return FilterCheckResult.FAIL_FORBIDDEN;
    }

    return FilterCheckResult.FAIL_METHOD_NOT_ALLOWED;
  }

  private FilterCheckResult filterCheckUser(String requestAddress, String requestMethod, String requestURI, String jwt) {
    return FilterCheckResult.SUCCESS;
  }

  private FilterCheckResult filterCheckAdmin(String requestAddress, String requestMethod, String requestURI, String jwt) {
    return FilterCheckResult.SUCCESS;
  }

  private FilterCheckResult filterCheckSystem(String requestAddress, String requestMethod, String requestURI, String jwt) {
    return FilterCheckResult.SUCCESS;
  }
}
