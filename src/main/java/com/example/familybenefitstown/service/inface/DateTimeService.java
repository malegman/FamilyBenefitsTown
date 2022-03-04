package com.example.familybenefitstown.service.inface;

import com.example.familybenefitstown.exception.DateFormatException;
import com.example.familybenefitstown.exception.DateTimeException;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

/**
 * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
 */
public interface DateTimeService {

  /**
   * Возвращает дату истечения срока жизни. Дата формируется из текущего времени и прибавленных секунд
   * @param expireSec число секунд, по окончанию которых истечет срок
   * @return дата истечения срока жизни
   */
  Date getExpiration(long expireSec);

  /**
   * Преобразует строку формата "dd.mm.yyyy" в дату
   * @param userBirth дата в строковом виде
   * @return преобразованная строка в формат даты
   * @throws DateFormatException если полученная строка не соответствует формату "dd.mm.yyyy"
   */
  LocalDate strToDate(String userBirth) throws DateFormatException;

  /**
   * Проверяет дату на предшествие текущей дате
   * @param dateCheck проверяемая дата
   * @throws DateTimeException если проверяемая дата позже текущей даты
   */
  void checkDateBeforeNow(LocalDate dateCheck) throws DateTimeException;

  /**
   * Проверяет множество дат на предшествие текущей дате
   * @param dateSet множество проверяемых дат
   * @throws DateTimeException если проверяемая дата позже текущей даты
   */
  void checkDateBeforeNow(Set<LocalDate> dateSet) throws DateTimeException;

  /**
   * Проверяет, был ли день рождения после проверяемой даты
   * @param dateBirth дата рождения
   * @param dateCheck проверяемая дата
   * @throws DateTimeException если день рождения был после проверяемой даты
   */
  void checkBirthdayBefore(LocalDate dateBirth, LocalDate dateCheck) throws DateTimeException;

  /**
   * Проверяет, был ли дни рождения после проверяемой даты
   * @param dateBirthSet множество дат рождения
   * @param dateCheck проверяемая дата
   * @throws DateTimeException если один из дней рождения был после проверяемой даты
   */
  void checkBirthdayBefore(Set<LocalDate> dateBirthSet, LocalDate dateCheck) throws DateTimeException;
}

