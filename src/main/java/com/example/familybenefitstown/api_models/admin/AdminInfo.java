package com.example.familybenefitstown.api_models.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Информация об администраторе
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInfo {

  /**
   * ID администратора
   */
  @JsonProperty("id")
  private String id;

  /**
   * Имя администратора
   */
  @JsonProperty("name")
  private String name;

  /**
   * Электронная почта администратора
   */
  @JsonProperty("email")
  private String email;

  /**
   * Список названий ролей администратора
   */
  @JsonProperty("nameRoleSet")
  private List<String> nameRoleSet;
}
