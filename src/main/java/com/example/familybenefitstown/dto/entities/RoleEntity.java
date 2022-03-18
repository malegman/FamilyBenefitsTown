package com.example.familybenefitstown.dto.entities;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Модель записи таблицы "role"
 */
@Entity
@Table(name = "role", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class RoleEntity {

  /**
   * ID роли
   */
  @NonNull
  @Id
  @Column(name = "id")
  private String id;

  /**
   * Название роли
   */
  @NonNull
  @Column(name = "name")
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    RoleEntity roleEntity = (RoleEntity) o;
    return id.equals(roleEntity.id) && name.equals(roleEntity.name);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

