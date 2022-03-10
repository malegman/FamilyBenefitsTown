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
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_roles", schema = "family_benefit_town",
      joinColumns = @JoinColumn(name = "id_user"),
      inverseJoinColumns = @JoinColumn(name = "id_role"))
  private Set<RoleEntity> roleEntitySet;

  /**
   * Добавляет пользователю роль
   * @param role добавляемая роль
   */
  public void addRole(RoleEntity role) {
    roleEntitySet.add(role);
  }

  /**
   * Проверяет наличие роли у пользователя
   * @param role роль, наличие которой необходимо проверить
   * @return true, если пользователь имеет указанную роль
   */
  public boolean hasRole(RoleEntity role) {
    return roleEntitySet.contains(role);
  }

  /**
   * Удаляет у пользователя роль
   * @param role удаляемая роль
   */
  public void removeRole(RoleEntity role) {
    roleEntitySet.remove(role);
  }
}
