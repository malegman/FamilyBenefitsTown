package com.example.familybenefitstown.dto.entities;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.List;

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

  /**
   * Список пользователей с данной ролью
   */
  @ManyToMany(mappedBy = "roleEntityList")
  @ToString.Exclude
  private List<UserEntity> userEntityList;
}

