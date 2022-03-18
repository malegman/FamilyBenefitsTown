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
 * Модель записи таблицы "refresh_token"
 */
@Entity
@Table(name = "refresh_token", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class RefreshTokenEntity {

  /**
   * ID пользователя
   */
  @NonNull
  @Id
  @Column(name = "id_user")
  private String idUser;

  /**
   * Токен доступа
   */
  @NonNull
  @Column(name = "token")
  private String token;

  /**
   * Время истечения срока токена восстановления
   */
  @NonNull
  @Column(name = "date_expiration")
  private LocalDateTime dateExpiration;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    RefreshTokenEntity refreshTokenEntity = (RefreshTokenEntity) o;
    return idUser.equals(refreshTokenEntity.idUser) && token.equals(refreshTokenEntity.token);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
