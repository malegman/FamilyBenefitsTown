package com.example.familybenefitstown.api_models.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект запроса для сохранения администратора, создания или обновления
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSave {

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
}
