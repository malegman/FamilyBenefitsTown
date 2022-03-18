package com.example.familybenefitstown.res_part_rest_api.dto.entities;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Модель записи таблицы "login_code"
 */
@Entity
@Table(name = "login_code", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class LoginCodeEntity {

  /**
   * ID пользователя
   */
  @NonNull
  @Id
  @Column(name = "id_user")
  private String idUser;

  /**
   * Код для входа в систему
   */
  @Column(name = "code")
  private int code;

  /**
   * Время истечения срока кода
   */
  @NonNull
  @Column(name = "date_expiration")
  private LocalDateTime dateExpiration;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    LoginCodeEntity loginCodeEntity = (LoginCodeEntity) o;
    return idUser.equals(loginCodeEntity.idUser) && code == loginCodeEntity.code;
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
