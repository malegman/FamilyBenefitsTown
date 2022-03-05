package com.example.familybenefitstown.security.web.config;

import com.example.familybenefitstown.security.web.filter.FilterFB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final FilterFB filterFB;

  /**
   * Конструктор для инициализации фильтра
   * @param filterFB фильтр всех входящих http запросов
   */
  @Autowired
  public WebSecurityConfig(FilterFB filterFB) {
    this.filterFB = filterFB;
  }

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
        .addFilterAt(filterFB, UsernamePasswordAuthenticationFilter.class);
  }
}
