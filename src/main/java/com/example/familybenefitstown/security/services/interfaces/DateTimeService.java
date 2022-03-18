package com.example.familybenefitstown.security.services.interfaces;

import com.example.familybenefitstown.exceptions.DateFormatException;
import com.example.familybenefitstown.exceptions.DateTimeException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
 */
public interface DateTimeService {

  /**
   * Возвращает дату истечения срока жизни. Дата формируется из текущего времени и прибавленных секунд
   * @param expireSec число секунд, по окончанию которых истечет срок
   * @return дата истечения срока жизни
   */
  LocalDateTime getExpiration(long expireSec);

  /**
   * Преобразует строку формата "dd.mm.yyyy" в дату
   * @param userBirth дата в строковом виде
   * @return преобразованная строка в формат даты
   * @throws DateFormatException если полученная строка не соответствует формату "dd.mm.yyyy"
   */
  LocalDate strToDate(String userBirth) throws DateFormatException;

  /**
   * Преобразует строки формата "dd.mm.yyyy" в даты
   * @param userBirthList список дат в строковом виде
   * @return преобразованная строка в формат даты
   * @throws DateFormatException если одна из полученных строк не соответствует формату "dd.mm.yyyy"
   */
  List<LocalDate> strToDate(List<String> userBirthList) throws DateFormatException;

  /**
   * Проверяет дату на предшествие текущей дате
   * @param dateCheck проверяемая дата
   * @throws DateTimeException если проверяемая дата позже текущей даты
   */
  void checkDateBeforeNow(LocalDate dateCheck) throws DateTimeException;

  /**
   * Проверяет список дат на предшествие текущей дате
   * @param dateList множество проверяемых дат
   * @throws DateTimeException если проверяемая дата позже текущей даты
   */
  void checkDateBeforeNow(List<LocalDate> dateList) throws DateTimeException;

  /**
   * Проверяет текущее время на предшествие проверяемому времени
   * @param dateTimeCheck проверяемое время
   * @throws DateTimeException если текущее время позже проверяемого
   */
  void checkDateTimeAfterNow(LocalDateTime dateTimeCheck) throws DateTimeException;

  /**
   * Проверяет, был ли день рождения после проверяемой даты
   * @param dateBirth дата рождения
   * @param dateCheck проверяемая дата
   * @throws DateTimeException если день рождения был после проверяемой даты
   */
  void checkBirthdayBefore(LocalDate dateBirth, LocalDate dateCheck) throws DateTimeException;
}

