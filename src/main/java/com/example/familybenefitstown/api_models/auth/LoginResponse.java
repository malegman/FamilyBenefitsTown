package com.example.familybenefitstown.api_models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
   * Список названий ролей пользователя
   */
  @JsonProperty("nameRoleUserList")
  private List<String> nameRoleUserList;
}
