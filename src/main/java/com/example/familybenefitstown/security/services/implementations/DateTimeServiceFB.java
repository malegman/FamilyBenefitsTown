package com.example.familybenefitstown.security.services.implementations;

import com.example.familybenefitstown.exceptions.DateFormatException;
import com.example.familybenefitstown.exceptions.DateTimeException;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.security.services.interfaces.DateTimeService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса, который предоставляет методы для работы с датой и временем
 */
@Service
public class DateTimeServiceFB implements DateTimeService {

  /**
   * Возвращает дату истечения срока жизни. Дата формируется из текущего времени и прибавленных секунд
   * @param expireSec число секунд, по окончанию которых истечет срок
   * @return дата истечения срока жизни
   */
  @Override
  public LocalDateTime getExpiration(long expireSec) {

    return LocalDateTime.now().plusSeconds(expireSec);
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
      return LocalDate.from((TemporalAccessor) R.SIMPLE_DATE_FORMAT.parse(userBirth));
    } catch (ParseException e) {
      throw new DateFormatException(String.format(
          "The string \"%s\" doesn't match the date format \"dd.mm.yyyy\"", userBirth));
    }
  }

  /**
   * Преобразует строки формата "dd.mm.yyyy" в даты
   *
   * @param userBirthList список дат в строковом виде
   * @return преобразованная строка в формат даты
   * @throws DateFormatException если одна из полученных строк не соответствует формату "dd.mm.yyyy"
   */
  @Override
  public List<LocalDate> strToDate(List<String> userBirthList) throws DateFormatException {

    List<LocalDate> localDateList = new ArrayList<>(userBirthList.size());
    for (String userBirth : userBirthList) {
      localDateList.add(strToDate(userBirth));
    }

    return localDateList;
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
   * @param dateList множество проверяемых дат
   * @throws DateTimeException если проверяемая дата позже текущей даты
   */
  @Override
  public void checkDateBeforeNow(List<LocalDate> dateList) throws DateTimeException {

    for (LocalDate date : dateList) {
      checkDateBeforeNow(date);
    }
  }

  /**
   * Проверяет текущее время на предшествие проверяемому времени
   * @param dateTimeCheck проверяемое время
   * @throws DateTimeException если текущее время позже проверяемого
   */
  @Override
  public void checkDateTimeAfterNow(LocalDateTime dateTimeCheck) throws DateTimeException {

    LocalDateTime dateTimeCurrent = LocalDateTime.now();
    if (dateTimeCurrent.isAfter(dateTimeCheck)) {
      throw new DateTimeException(String.format(
          "Current time \"%s\" is after check time \"%s\"", dateTimeCurrent, dateTimeCheck));
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
}

