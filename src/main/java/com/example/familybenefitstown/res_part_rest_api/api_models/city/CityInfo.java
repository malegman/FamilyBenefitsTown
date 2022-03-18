package com.example.familybenefitstown.res_part_rest_api.api_models.city;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о городе
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityInfo {

  /**
   * ID города
   */
  @JsonProperty("id")
  private String id;

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
