package com.example.familybenefitstown.dto.entities;

import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;

/**
 * Модель записи таблицы "cityEntity"
 */
@Entity
@Table(name = "city", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class CityEntity extends ObjectEntity {

  /**
   * ID города
   */
  @NonNull
  @Id
  @Column(name = "id")
  private String id;

  /**
   * Название города
   */
  @NonNull
  @Column(name = "name")
  private String name;

  /**
   * Информация города
   */
  @Nullable
  @Column(name = "info")
  private String info;

  /**
   * Список пользователей с данным городом
   */
  @OneToMany(mappedBy = "cityEntity")
  @ToString.Exclude
  private List<UserEntity> userEntityList;

  /**
   * Конструктор для создания модели по ID
   * @param id ID города
   */
  public CityEntity(@NonNull String id) {
    this.id = id;
  }
}
