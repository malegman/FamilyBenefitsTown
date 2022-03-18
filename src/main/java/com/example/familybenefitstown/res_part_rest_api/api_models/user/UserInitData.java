package com.example.familybenefitstown.res_part_rest_api.api_models.user;

import com.example.familybenefitstown.res_part_rest_api.api_models.common.ObjectShortInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Дополнительные данные для создания или обновления пользователя.
 * Содержат в себе список кратких информаций о городах
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInitData {

  /**
   * Список кратких информаций о городах
   */
  @JsonProperty("shortCitySet")
  private List<ObjectShortInfo> shortCitySet;
}
