package com.example.familybenefitstown.part_res_rest_api.converters;

import com.example.familybenefitstown.part_res_rest_api.api_models.city.CityInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.city.CitySave;
import com.example.familybenefitstown.part_res_rest_api.api_models.common.ObjectShortInfo;
import com.example.familybenefitstown.dto.entities.CityEntity;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.security.RandomValue;

import java.util.function.Function;

/**
 * Класс преобразования модели таблицы "city" в другие объекты и получения из других объектов, обрабатывая строковые поля для БД.
 */
public class CityDBConverter {

  /**
   * Преобразует объект запроса на сохранение города в модель таблицы "city", обрабатывая строковые поля для БД
   * @param idCity ID города. Если {@code null}, значение ID генерируется.
   * @param citySave объект запроса на сохранение города
   * @param prepareDBFunc функция обработки строки для БД
   * @return модель таблицы "city"
   * @throws InvalidStringException если строковое поле объекта запроса не содержит букв или цифр
   */
  public static CityEntity fromSave(String idCity, CitySave citySave, Function<String, String> prepareDBFunc) throws InvalidStringException {

    if (citySave == null) {
      return new CityEntity();
    }

    return CityEntity
        .builder()
        .id(idCity != null
                ? prepareDBFunc.apply(idCity)
                : RandomValue.randomString(R.ID_LENGTH))
        .name(prepareDBFunc.apply(FieldConverter.withSymbolsField(citySave.getName(), "name", true)))
        .info(prepareDBFunc.apply(FieldConverter.withSymbolsField(citySave.getInfo(), "info", false)))
        .build();
  }

  /**
   * Преобразует модель таблицы "city" в объект информации о городе
   * @param cityEntity модель таблицы "city"
   * @return информация о городе
   */
  public static CityInfo toInfo(CityEntity cityEntity) {

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
  public static ObjectShortInfo toShortInfo(CityEntity cityEntity) {

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

