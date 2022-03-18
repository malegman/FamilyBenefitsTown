package com.example.familybenefitstown.auth_part.config;

import com.example.familybenefitstown.auth_part.filter.AllRequestsFilterFB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Конфигурация web безопасности
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final AllRequestsFilterFB allRequestsFilterFB;

  /**
   * Конструктор для инициализации фильтра
   * @param allRequestsFilterFB фильтр всех входящих http запросов
   */
  @Autowired
  public WebSecurityConfig(AllRequestsFilterFB allRequestsFilterFB) {
    this.allRequestsFilterFB = allRequestsFilterFB;
  }

  /**
   * Возвращает реализацию {@link UserDetailsService}, которая возвращает {@code null} в методе {@code loadUserByUsername}.
   * @return Реализация сервиса
   */
  @Bean
  protected UserDetailsService getUserDetailsService() {
    return username -> null;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        .httpBasic().disable()
        .csrf().disable()
        .anonymous().disable()
        .securityContext().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        
        .and()
        .formLogin().disable()
        .logout().disable()

        .addFilterAt(allRequestsFilterFB, UsernamePasswordAuthenticationFilter.class);
  }
}
