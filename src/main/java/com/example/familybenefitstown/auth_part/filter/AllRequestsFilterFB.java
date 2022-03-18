package com.example.familybenefitstown.auth_part.filter;

import com.example.familybenefitstown.auth_part.models.RequestCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр всех входящих http запросов
 */
@Slf4j
@Component
public class AllRequestsFilterFB extends OncePerRequestFilter {

  /**
   * Класс для проверки запроса на аутентификацию и авторизацию
   */
  private final RequestChecker requestChecker;

  /**
   * Конструктор для инициализации сервисов
   * @param requestChecker класс для проверки запроса на аутентификацию и авторизацию
   */
  @Autowired
  public AllRequestsFilterFB(RequestChecker requestChecker) {
    this.requestChecker = requestChecker;
  }

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

    String requestURI = request.getRequestURI();

    RequestCheckResponse checkResponse = new RequestCheckResponse(false, response);

    if (requestURI.startsWith("/cities")) {
      checkResponse = requestChecker.checkCity(request, response);
    } else if (requestURI.startsWith("/users")) {
      checkResponse = requestChecker.checkUser(request, response);
    } else if (requestURI.startsWith("/admins")) {
      checkResponse = requestChecker.checkAdmin(request, response);
    } else if (requestURI.startsWith("/auth")){
      checkResponse = requestChecker.checkAuth(request, response);
    } else if (requestURI.startsWith("/sa")){
      checkResponse = requestChecker.checkSuperAdmin(request, response);
    }

    if (checkResponse.isSuccess()) {
      filterChain.doFilter(request, checkResponse.getResponse());
    }
  }
}
