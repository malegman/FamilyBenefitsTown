package com.example.familybenefitstown.res_part_rest_api.dto.entities;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Модель записи таблицы "child_birth"
 */
@Entity
@Table(name = "child_birth", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ChildBirthEntity {

  /**
   * ID рождения ребенка
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
    ChildBirthEntity childBirthEntity = (ChildBirthEntity) o;
    return id.equals(childBirthEntity.id) && dateBirth.equals(childBirthEntity.dateBirth);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
