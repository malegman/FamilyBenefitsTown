package com.example.familybenefitstown.dto.entities;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Модель записи таблицы "child"
 */
@Entity
@Table(name = "child", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ChildEntity {

  /**
   * ID ребенка
   */
  @NonNull
  @Id
  @Column(name = "id")
  private String id;

  /**
   * Дата рождения ребенка
   */
  @NonNull
  @Column(name = "date_birth")
  private LocalDate dateBirth;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    ChildEntity childEntity = (ChildEntity) o;
    return id.equals(childEntity.id) && dateBirth.equals(childEntity.dateBirth);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
