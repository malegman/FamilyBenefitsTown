package com.example.familybenefitstown.api_models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Информация о пользователе
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

  /**
   * ID пользователя
   */
  @JsonProperty("id")
  private String id;

  /**
   * Имя пользователя
   */
  @JsonProperty("name")
  private String name;

  /**
   * Электронная почта пользователя
   */
  @JsonProperty("email")
  private String email;

  /**
   * Список дат рождений детей пользователя
   */
  @JsonProperty("birthDateChildren")
  private List<String> birthDateChildren;

  /**
   * Дата рождения пользователя
   */
  @JsonProperty("dateBirth")
  private String dateBirth;

  /**
   * Список названий ролей пользователя
   */
  @JsonProperty("nameRoleList")
  private List<String> nameRoleList;

  /**
   * Название города пользователя
   */
  @JsonProperty("nameCity")
  private String nameCity;
}
