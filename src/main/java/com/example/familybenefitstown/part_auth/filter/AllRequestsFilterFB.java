package com.example.familybenefitstown.part_auth.filter;

import com.example.familybenefitstown.part_auth.filter.request_handlers.*;
import com.example.familybenefitstown.part_auth.models.RequestHandlerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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
@Component
public class AllRequestsFilterFB extends OncePerRequestFilter {

  /**
   * Обрабатывает запросы вида "/api/admins" на основе их данных аутентификации и авторизации.
   */
  private final AdminRequestHandler adminRequestHandler;
  /**
   * Обрабатывает запросы вида "/api/auth" на основе их данных аутентификации и авторизации.
   */
  private final AuthRequestHandler authRequestHandler;
  /**
   * Обрабатывает запросы вида "/api/cities" на основе их данных аутентификации и авторизации.
   */
  private final CityRequestHandler cityRequestHandler;
  /**
   * Обрабатывает запросы вида "/api/sa" на основе их данных аутентификации и авторизации.
   */
  private final SuperAdminRequestHandler superAdminRequestHandler;
  /**
   * Обрабатывает запросы вида "/api/users" на основе их данных аутентификации и авторизации.
   */
  private final UserRequestHandler userRequestHandler;

  /**
   * Конструктор для инициализации сервисов
   * @param adminRequestHandler обрабатывает запросы вида "/api/admins" на основе их данных аутентификации и авторизации
   * @param authRequestHandler обрабатывает запросы вида "/api/auth" на основе их данных аутентификации и авторизации
   * @param cityRequestHandler обрабатывает запросы вида "/api/cities" на основе их данных аутентификации и авторизации
   * @param superAdminRequestHandler обрабатывает запросы вида "/api/sa" на основе их данных аутентификации и авторизации
   * @param userRequestHandler обрабатывает запросы вида "/api/users" на основе их данных аутентификации и авторизации
   */
  @Autowired
  public AllRequestsFilterFB(AdminRequestHandler adminRequestHandler,
                             AuthRequestHandler authRequestHandler,
                             CityRequestHandler cityRequestHandler,
                             SuperAdminRequestHandler superAdminRequestHandler,
                             UserRequestHandler userRequestHandler) {
    this.adminRequestHandler = adminRequestHandler;
    this.authRequestHandler = authRequestHandler;
    this.cityRequestHandler = cityRequestHandler;
    this.superAdminRequestHandler = superAdminRequestHandler;
    this.userRequestHandler = userRequestHandler;
  }

  @Override
  public void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {

    String requestURI = request.getRequestURI();

    RequestHandlerResponse checkResponse = new RequestHandlerResponse(false, response);

    if (requestURI.startsWith("/api/cities")) {
      checkResponse = cityRequestHandler.handle(request, response);
    } else if (requestURI.startsWith("/api/users")) {
      checkResponse = userRequestHandler.handle(request, response);
    } else if (requestURI.startsWith("/api/admins")) {
      checkResponse = adminRequestHandler.handle(request, response);
    } else if (requestURI.startsWith("/api/auth")){
      checkResponse = authRequestHandler.handle(request, response);
    } else if (requestURI.startsWith("/api/sa")){
      checkResponse = superAdminRequestHandler.handle(request, response);
    }

    if (checkResponse.isSuccess()) {
      filterChain.doFilter(request, checkResponse.getResponse());
    }
  }
}
