package com.example.familybenefitstown.dto.entity;

import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

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
   * Дата последнего выбора критерий пользователя
   */
  @Nullable
  @Column(name = "date_select_criterion")
  private LocalDate dateSelectCriterion;

  /**
   * Флаг свежести подобранных пособий
   */
  @Column(name = "is_fresh_benefits")
  private boolean isFreshBenefits;

  /**
   * Город пользователя
   */
  @Nullable
  @ManyToOne
  @JoinColumn(name = "id_city")
  private CityEntity cityEntity;

  /**
   * Множество детей пользователя
   */
  @NonNull
  @ToString.Exclude
  @ManyToMany
  @JoinTable(
      name = "users_children", schema = "family_benefit_town",
      joinColumns = @JoinColumn(name = "id_user"),
      inverseJoinColumns = @JoinColumn(name = "id_child"))
  private Set<ChildEntity> childEntitySet;

  /**
   * Множество ролей пользователя
   */
  @NonNull
  @ToString.Exclude
  @ManyToMany
  @JoinTable(
      name = "users_roles", schema = "family_benefit_town",
      joinColumns = @JoinColumn(name = "id_user"),
      inverseJoinColumns = @JoinColumn(name = "id_role"))
  private Set<RoleEntity> roleEntitySet;

  /**
   * Добавляет пользователю роль по её названию
   * @param nameRole название добавляемой роли
   */
  public void addRole(String nameRole) {
    roleEntitySet.add(RoleEntity.builder().name(nameRole).build());
  }

  /**
   * Проверяет наличие роли у пользователя
   * @param nameRole название роли, наличие которой необходимо проверить
   * @return true, если пользователь имеет роль с указанным именем
   */
  public boolean hasRole(String nameRole) {

    for (RoleEntity roleEntity : roleEntitySet) {
      if (roleEntity.getName().equals(nameRole)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Удаляет у пользователя роль по её названию
   * @param nameRole название удаляемой роли
   */
  public void removeRole(String nameRole) {

    RoleEntity roleEntityToRemove = null;

    for (RoleEntity roleEntity : roleEntitySet) {
      if (roleEntity.getName().equals(nameRole)) {
        roleEntityToRemove = roleEntity;
        break;
      }
    }

    if (roleEntityToRemove != null) {
      roleEntitySet.remove(roleEntityToRemove);
    }
  }
}
