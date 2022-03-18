package com.example.familybenefitstown.part_res_rest_api.api_models.city;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект запроса для сохранения города, создания или обновления
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitySave {

  /**
   * Название города
   */
  @JsonProperty("name")
  private String name;

  /**
   * Информация города
   */
  @JsonProperty("info")
  private String info;
}
