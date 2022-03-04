package com.example.familybenefitstown.api_model.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Объект ответа на вход в систему
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  /**
   * ID пользователя
   */
  @JsonProperty("idUser")
  private String idUser;

  /**
   * Имя пользователя
   */
  @JsonProperty("nameUser")
  private String nameUser;

  /**
   * Множество названий ролей пользователя
   */
  @JsonProperty("nameRoleUserSet")
  private Set<String> nameRoleUserSet;
}
