package com.example.familybenefitstown.security.web.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Данные пользователя, хранимые в jwt, необходимые для аутентификации и авторизации
 */
@Getter
@Builder
public class JwtUserData {

  /**
   * ID пользователя
   */
  private String idUser;

  /**
   * Множество названий ролей пользователя
   */
  private Set<String> nameRoleSet;

  /**
   * IP-адрес, с которого пользователь вошел в систему
   */
  private String address;

  /**
   * Преобразует строку в объект данных авторизации в jwt
   * @param content строка для преобразования
   * @return данные авторизации
   */
  public static JwtUserData fromString(String content) {

    Pattern patternAuth = Pattern.compile("^" +
                                              "id=(?<id>[0-9a-zA-Z]{20})" +
                                              "roles=(?<roles>[A-Z_,]+)" +
                                              "ip=(?<ip>((25[0-5]|2[0-4][0-9]|[0-9]|[1-9][0-9]|1[0-9][0-9])\\.){3}(25[0-5]|2[0-4][0-9]|[0-9]|[1-9][0-9]|1[0-9][0-9]))" +
                                              "$");
    Matcher matcherAuth = patternAuth.matcher(content);

    return JwtUserData
        .builder()
        .idUser(matcherAuth.group("id"))
        .nameRoleSet(new HashSet<>(List.of(matcherAuth.group("roles").split(","))))
        .address(matcherAuth.group("ip"))
        .build();
  }

  @Override
  public String toString() {

    return String.format("id=%sroles=%sip=%s", idUser, String.join(",", nameRoleSet), address);
  }
}
