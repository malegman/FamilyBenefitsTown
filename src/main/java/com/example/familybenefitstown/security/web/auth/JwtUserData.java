package com.example.familybenefitstown.security.web.auth;

import com.example.familybenefitstown.resources.R;
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
   * Преобразует строку в объект данных авторизации в jwt
   * @param content строка для преобразования
   * @return данные авторизации, {@code null} если не удалось преобразовать строку в объект
   */
  public static JwtUserData fromString(String content) {

    Pattern patternAuth = Pattern.compile(String.format(
        "^id=(?<id>[0-9a-zA-Z]{%s})roles=(?<roles>[A-Z_,]+)$", R.ID_LENGTH));
    Matcher matcherData = patternAuth.matcher(content);

    if (!matcherData.matches()) {
      return null;
    }

    return JwtUserData
        .builder()
        .idUser(matcherData.group("id"))
        .nameRoleSet(new HashSet<>(List.of(matcherData.group("roles").split(","))))
        .build();
  }

  @Override
  public String toString() {

    return String.format("id=%sroles=%s", idUser, String.join(",", nameRoleSet));
  }
}
