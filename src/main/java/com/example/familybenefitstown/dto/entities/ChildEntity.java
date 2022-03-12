package com.example.familybenefitstown.dto.entities;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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
public class ChildEntity extends ObjectEntity {

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

  /**
   * Список пользователей с данным ребенком
   */
  @ManyToMany(mappedBy = "childEntityList")
  @ToString.Exclude
  private List<UserEntity> userEntityList;
}
