package com.example.familybenefitstown.security;

import java.security.SecureRandom;

/**
 * Генератор случайных значений
 */
public class RandomValue {

  private static final char[] RANDOM_STRING_SYMBOLS = {'0','1','2','3','4','5','6','7','8','9',
      'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
      'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

  /**
   * Генерирует строку из символов A-Za-z0-9 указанной длины
   * @param length длина строки
   * @return сгенерированная строка
   */
  public static String randomString(int length) {

    if (length < 0) {
      throw new IllegalArgumentException(String.format("Argument \"length\" %s is less than 0.", length));
    }

    byte[] randBytes = new byte[length];
    StringBuilder resultString = new StringBuilder();

    int countRefSymbols = RANDOM_STRING_SYMBOLS.length;

    // Для пропорционального приведения диапазона [0-255] к диапазону [0-REFRESH_SYMBOLS.length]
    double part = countRefSymbols / 256.0;

    // Получение случайных значений
    (new SecureRandom()).nextBytes(randBytes);

    // Заполнение токена символами
    for (int randI = 0; randI < length; randI++) {
      resultString.append(RANDOM_STRING_SYMBOLS[(int) Math.floor((randBytes[randI] + 128) * part)]);
    }

    return resultString.toString();
  }

  /**
   * Генерирует число указанной длины
   * @param length длина числа в символах
   * @return сгенерированное число
   */
  public static int randomInteger(int length) {

    if (length < 0) {
      throw new IllegalArgumentException(String.format("Argument \"length\" %s is less than 0.", length));
    }

    byte[] randBytes = new byte[length];

    // Для пропорционального приведения диапазона [0-255] к диапазону [1-9], для первой цифры кода
    double part1 = 9 / 256.0;
    // Для пропорционального приведения диапазона [0-255] к диапазону [0-9]
    double part = 10 / 256.0;

    // Получение случайных значений
    (new SecureRandom()).nextBytes(randBytes);

    // Добавление первой цифры из диапазона [1-9]
    int resultInteger = ((int) Math.floor((randBytes[0] + 128) * part1)) + 1;
    // Заполнение числа оставшимися цифрами
    for (int randI = 1, tempVal = 10; randI < length; randI++, tempVal *= 10) {
      resultInteger += tempVal * (int) Math.floor((randBytes[randI] + 128) * part);
    }

    return resultInteger;
  }
}
