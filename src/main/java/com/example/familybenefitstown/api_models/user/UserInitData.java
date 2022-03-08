package com.example.familybenefitstown.api_models.user;

import com.example.familybenefitstown.api_models.common.ObjectShortInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Дополнительные данные для создания или обновления пользователя.
 * Содержат в себе множества кратких информаций о городах и полных критериях
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInitData {

  /**
   * Множество кратких информаций о городах
   */
  @JsonProperty("shortCitySet")
  private Set<ObjectShortInfo> shortCitySet;
}
