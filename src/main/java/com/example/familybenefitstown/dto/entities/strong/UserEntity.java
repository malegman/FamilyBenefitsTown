package com.example.familybenefitstown.dto.entities.strong;

import com.example.familybenefitstown.dto.entities.ObjectEntity;
import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Модель записи таблицы "user"
 */
@Entity
@Table(name = "user", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class UserEntity extends ObjectEntity {

  /**
   * ID пользователя
   */
  @NonNull
  @Id
  @Column(name = "id")
  private String id;

  /**
   * Имя пользователя
   */
  @NonNull
  @Column(name = "name")
  private String name;

  /**
   * Электронная почта пользователя
   */
  @NonNull
  @Column(name = "email")
  private String email;

  /**
   * Дата рождения пользователя
   */
  @Nullable
  @Column(name = "date_birth")
  private LocalDate dateBirth;

  /**
   * Город пользователя
   */
  @Nullable
  @ManyToOne
  @JoinColumn(name = "id_city")
  private CityEntity cityEntity;
}
