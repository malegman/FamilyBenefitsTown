package com.example.familybenefitstown.converters;

import com.example.familybenefitstown.api_models.city.CityInfo;
import com.example.familybenefitstown.api_models.city.CitySave;
import com.example.familybenefitstown.api_models.common.ObjectShortInfo;
import com.example.familybenefitstown.dto.entities.strong.CityEntity;

import java.util.function.Function;

/**
 * Класс преобразования модели таблицы "city" в другие объекты и получения из других объектов, обрабатывая строковые поля для БД.
 */
public class CityDBConverter {

  /**
   * Преобразует объект запроса на сохранение города в модель таблицы "city", обрабатывая строковые поля для БД
   * @param citySave объект запроса на сохранение города
   * @param prepareDBFunc функция обработки строки для БД
   * @return модель таблицы "city"
   */
  static public CityEntity fromSave(CitySave citySave, Function<String, String> prepareDBFunc) {

    if (citySave == null) {
      return new CityEntity();
    }

    return CityEntity
        .builder()
        .name(prepareDBFunc.apply(citySave.getName()))
        .info(prepareDBFunc.apply(citySave.getInfo()))
        .build();
  }

  /**
   * Преобразует модель таблицы "city" в объект информации о городе
   * @param cityEntity модель таблицы "city"
   * @return информация о городе
   */
  static public CityInfo toInfo(CityEntity cityEntity) {

    if (cityEntity == null) {
      return new CityInfo();
    }

    return CityInfo
        .builder()
        .id(cityEntity.getId())
        .name(cityEntity.getName())
        .info(cityEntity.getInfo())
        .build();
  }

  /**
   * Преобразует модель таблицы "city" в объект краткой информации об объекте
   * @param cityEntity модель таблицы "city"
   * @return краткая информация о городе
   */
  static public ObjectShortInfo toShortInfo(CityEntity cityEntity) {

    if (cityEntity == null) {
      return new ObjectShortInfo();
    }

    return ObjectShortInfo
        .builder()
        .idObject(cityEntity.getId())
        .nameObject(cityEntity.getName())
        .build();
  }
}

