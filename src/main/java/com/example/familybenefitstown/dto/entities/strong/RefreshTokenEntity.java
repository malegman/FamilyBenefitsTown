package com.example.familybenefitstown.dto.entities.strong;

import com.example.familybenefitstown.dto.entities.ObjectEntity;
import lombok.*;
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
public class RefreshTokenEntity extends ObjectEntity {

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
}
