package com.example.familybenefitstown.part_res_rest_api.api_models.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Краткая информация об объекте
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectShortInfo {

  /**
   * ID объекта
   */
  @JsonProperty("idObject")
  private String idObject;

  /**
   * Название объекта
   */
  @JsonProperty("nameObject")
  private String nameObject;
}
