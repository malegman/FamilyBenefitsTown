package com.example.familybenefitstown.dto.entities.strong;

import com.example.familybenefitstown.dto.entities.ObjectEntity;
import lombok.*;
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
}
