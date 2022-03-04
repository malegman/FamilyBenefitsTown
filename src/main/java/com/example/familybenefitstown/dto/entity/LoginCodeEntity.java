package com.example.familybenefitstown.dto.entity;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

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
public class LoginCodeEntity extends ObjectEntity {

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
  @NonNull
  @Column(name = "code")
  private BigInteger code;

  /**
   * Время истечения срока кода
   */
  @NonNull
  @Column(name = "date_expiration")
  private Date dateExpiration;
}
