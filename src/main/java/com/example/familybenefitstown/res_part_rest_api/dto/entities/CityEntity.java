package com.example.familybenefitstown.res_part_rest_api.dto.entities;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
public class CityEntity {

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
  @Column(name = "info")
  private String info;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    CityEntity cityEntity = (CityEntity) o;
    return id.equals(cityEntity.id) && name.equals(cityEntity.name);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
