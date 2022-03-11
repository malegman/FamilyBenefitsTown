package com.example.familybenefitstown.dto.entities.strong;

import com.example.familybenefitstown.dto.entities.ObjectEntity;
import lombok.*;
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
public class RoleEntity extends ObjectEntity {

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
}

