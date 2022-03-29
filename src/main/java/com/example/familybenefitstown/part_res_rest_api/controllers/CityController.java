package com.example.familybenefitstown.part_res_rest_api.controllers;

import com.example.familybenefitstown.part_res_rest_api.api_models.city.CityInfo;
import com.example.familybenefitstown.part_res_rest_api.api_models.city.CitySave;
import com.example.familybenefitstown.part_res_rest_api.api_models.common.ObjectShortInfo;
import com.example.familybenefitstown.exceptions.AlreadyExistsException;
import com.example.familybenefitstown.exceptions.InvalidStringException;
import com.example.familybenefitstown.exceptions.NotFoundException;
import com.example.familybenefitstown.part_res_rest_api.services.interfaces.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Контроллер запросов, связанных с городом
 */
@Slf4j
@RestController
public class CityController {

  /**
   * Интерфейс сервиса, управляющего объектом "город"
   */
  private final CityService cityService;

  /**
   * Конструктор для инициализации интерфейса сервиса
   * @param cityService интерфейс сервиса, управляющего объектом "город"
   */
  @Autowired
  public CityController(CityService cityService) {
    this.cityService = cityService;
  }

  /**
   * Обрабатывает GET запрос "/api/cities/all" на получение множества городов,
   * в которых есть учреждения и пособия.
   * Фильтр по названию или ID пособия.
   * Выполнить запрос может любой клиент
   * @param name Название города
   * @param request http запрос
   * @return множество городов, если запрос выполнен успешно, и код ответа
   */
  @GetMapping(
      value = "/api/cities",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<Set<ObjectShortInfo>> readAllFilter(@RequestParam(name = "name", required = false) String name,
                                                            HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} GET \"/api/cities?name={}\": Request in controller", requestAddress, name);

    Set<ObjectShortInfo> cityShortInfoSet = cityService.readAllFilter(name);
    return ResponseEntity.status(HttpStatus.OK).body(cityShortInfoSet);
  }

  /**
   * Обрабатывает POST запрос "/api/cities" на создание города.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_ADMIN"
   * @param citySave объект запроса для сохранения города
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PostMapping(
      value = "/api/cities",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> create(@RequestBody CitySave citySave,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} POST \"/api/cities\": Request in controller", requestAddress);

    // Если тело запроса пустое
    if (citySave == null) {
      log.warn("{} POST \"/api/cities\": Request body \"citySave\" is empty", requestAddress);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      cityService.create(citySave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (AlreadyExistsException | InvalidStringException e) {
      // Город с указанным названием существует.
      // Некорректное строковое поле объекта запроса.
      log.warn("{} POST \"/api/cities\": {}", requestAddress, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    } catch (NotFoundException e) {
      // Не найдены пособия
      log.warn("{} POST \"/api/cities\": {}", requestAddress, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает GET запрос "/api/cities/{id}" на получение информации о городе.
   * Выполнить запрос может любой клиент
   * @param idCity ID города
   * @param request http запрос
   * @return информация о городе, если запрос выполнен успешно, и код ответа
   */
  @GetMapping(
      value = "/api/cities/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseBody
  public ResponseEntity<CityInfo> read(@PathVariable(name = "id") String idCity,
                                       HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} GET \"/api/cities/{}\": Request in controller", requestAddress, idCity);

    try {
      CityInfo cityInfo = cityService.read(idCity);
      return ResponseEntity.status(HttpStatus.OK).body(cityInfo);

    } catch (NotFoundException e) {
      // Не найден город
      log.warn("{} GET \"/api/cities/{}\": {}", requestAddress, idCity, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Обрабатывает PUT запрос "/api/cities/{id}" на обновление города.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_ADMIN"
   * @param idCity ID города
   * @param citySave объект запроса для сохранения города
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @PutMapping(
      value = "/api/cities/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> update(@PathVariable(name = "id") String idCity,
                                  @RequestBody CitySave citySave,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} PUT \"/api/cities/{}\": Request in controller", requestAddress, idCity);

    // Если тело запроса пустое
    if (citySave == null) {
      log.warn("{} PUT \"/api/cities/{}\": Request body \"citySave\" is empty", requestAddress, idCity);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      cityService.update(idCity, citySave);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден город или не найдены пособия
      log.warn("{} PUT \"/api/cities/{}\": {}", requestAddress, idCity, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    } catch (AlreadyExistsException | InvalidStringException e) {
      // Город с отличным ID и данным названием уже существует.
      // Некорректное строковое поле объекта запроса.
      log.warn("{} PUT \"/api/cities/{}\": {}", requestAddress, idCity, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Обрабатывает DELETE запрос "/api/cities/{id}" на удаление городе.
   * Для выполнения запроса клиент должен быть аутентифицирован и иметь роль "ROLE_ADMIN"
   * @param idCity ID города
   * @param request http запрос
   * @return код ответа, результат обработки запроса
   */
  @DeleteMapping(
      value = "/api/cities/{id}")
  public ResponseEntity<?> delete(@PathVariable(name = "id") String idCity,
                                  HttpServletRequest request) {

    String requestAddress = request.getRemoteAddr();
    log.debug("{} DELETE \"/api/cities/{}\": Request in controller", requestAddress, idCity);

    try {
      cityService.delete(idCity);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } catch (NotFoundException e) {
      // Не найден город
      log.warn("{} DELETE \"/api/cities/{}\": {}", requestAddress, idCity, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
