package com.example.familybenefitstown.service.impl;

import com.example.familybenefitstown.exception.DateFormatException;
import com.example.familybenefitstown.exception.DateTimeException;
import com.example.familybenefitstown.service.inface.DateTimeService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * Реализация сервиса, который предоставляет методы для работы с датой и временем
 */
@Service
public class DateTimeServiceFB implements DateTimeService {

  /**
   * Формат даты для преобразования строки в дату и дату в строку
   */
  private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

  /**
   * Возвращает дату истечения срока жизни. Дата формируется из текущего времени и прибавленных секунд
   * @param expireSec число секунд, по окончанию которых истечет срок
   * @return дата истечения срока жизни
   */
  @Override
  public Date getExpiration(long expireSec) {

    return Date.from(LocalDateTime
                         .now()
                         .plusSeconds(expireSec)
                         .toInstant(ZoneOffset.UTC));
  }

  /**
   * Преобразует строку формата "dd.mm.yyyy" в дату
   * @param userBirth дата в строковом виде
   * @return преобразованная строка в формат даты
   * @throws DateFormatException если полученная строка не соответствует формату "dd.mm.yyyy"
   */
  @Override
  public LocalDate strToDate(String userBirth) throws DateFormatException {

    try {
      return LocalDate.from((TemporalAccessor) SIMPLE_DATE_FORMAT.parse(userBirth));
    } catch (ParseException e) {
      throw new DateFormatException(String.format(
          "The string \"%s\" doesn't match the date format \"dd.mm.yyyy\"", userBirth));
    }
  }

  /**
   * Проверяет дату на предшествие текущей дате
   * @param dateCheck проверяемая дата
   * @throws DateTimeException если проверяемая дата позже текущей даты
   */
  @Override
  public void checkDateBeforeNow(LocalDate dateCheck) throws DateTimeException {

    LocalDate dateCurrent = LocalDate.now();
    if (dateCheck.isAfter(dateCurrent)) {
      throw new DateTimeException(String.format(
          "The date \"%s\" is after current date \"%s\"", dateCheck, dateCurrent));
    }
  }

  /**
   * Проверяет множество дат на предшествие текущей дате
   * @param dateSet множество проверяемых дат
   * @throws DateTimeException если проверяемая дата позже текущей даты
   */
  @Override
  public void checkDateBeforeNow(Set<LocalDate> dateSet) throws DateTimeException {

    for (LocalDate date : dateSet) {
      checkDateBeforeNow(date);
    }
  }

  /**
   * Проверяет, был ли день рождения после проверяемой даты
   * @param dateBirth дата рождения
   * @param dateCheck проверяемая дата
   * @throws DateTimeException если день рождения был после проверяемой даты
   */
  @Override
  public void checkBirthdayBefore(LocalDate dateBirth, LocalDate dateCheck) throws DateTimeException {

    if (dateBirth.plusYears(LocalDate.now().getYear() - dateBirth.getYear()).isAfter(dateCheck)) {
      throw new DateTimeException(String.format(
          "The day of birthday of date \"%s\" was after check date \"%s\"", dateBirth, dateCheck));
    }
  }

  /**
   * Проверяет, был ли дни рождения после проверяемой даты
   * @param dateBirthSet множество дат рождения
   * @param dateCheck проверяемая дата
   * @throws DateTimeException если один из дней рождения был после проверяемой даты
   */
  @Override
  public void checkBirthdayBefore(Set<LocalDate> dateBirthSet, LocalDate dateCheck) throws DateTimeException {

    for (LocalDate dateBase : dateBirthSet) {
      checkBirthdayBefore(dateBase, dateCheck);
    }
  }
}

