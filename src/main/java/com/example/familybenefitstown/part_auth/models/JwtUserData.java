package com.example.familybenefitstown.part_auth.models;

import com.example.familybenefitstown.dto.entities.RoleEntity;
import com.example.familybenefitstown.resources.R;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
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
   * Список названий ролей пользователя
   */
  private List<String> nameRoleList;

  /**
   * Проверяет наличие роли в пользовательских данных из списка ролей
   * @param roleEntityList список моделей роли
   * @return true, если пользовательские данные содержат роль
   */
  public boolean hasRole(List<RoleEntity> roleEntityList) {

    boolean containsRole = false;

    for (RoleEntity roleEntity : roleEntityList) {
      containsRole = nameRoleList.contains(roleEntity.getName());
      if (containsRole) {
        break;
      }
    }

    return containsRole;
  }

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
        .nameRoleList(List.of(matcherData.group("roles").split(",")))
        .build();
  }

  @Override
  public String toString() {

    return String.format("id=%sroles=%s", idUser, String.join(",", nameRoleList));
  }
}
