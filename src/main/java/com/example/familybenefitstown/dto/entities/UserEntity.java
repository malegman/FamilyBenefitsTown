package com.example.familybenefitstown.dto.entities;

import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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
   * Список детей пользователя
   */
  @ManyToMany
  @JoinTable(
      name = "users_children", schema = "family_benefit_town",
      joinColumns = @JoinColumn(name = "id_user"),
      inverseJoinColumns = @JoinColumn(name = "id_child"))
  @ToString.Exclude
  private List<ChildEntity> childEntityList;

  /**
   * Список ролей пользователя
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_roles", schema = "family_benefit_town",
      joinColumns = @JoinColumn(name = "id_user"),
      inverseJoinColumns = @JoinColumn(name = "id_role"))
  private List<RoleEntity> roleEntityList;

  /**
   * Добавляет указанную роль данному пользователю.
   * Если модель роли равна {@code null} или пользователь имеет указанную роль, добавление не осуществляется
   * @param roleEntity модель добавляемой роли.
   */
  public void addRole(RoleEntity roleEntity) {

    if (roleEntity != null && !roleEntityList.contains(roleEntity)) {
      roleEntityList.add(roleEntity);
    }
  }

  /**
   * Удаляет указанную роль у данного пользователя
   * @param roleEntity модель удаляемой роли
   */
  public void deleteRole(RoleEntity roleEntity) {

    roleEntityList.remove(roleEntity);
  }

  /**
   * Проверяет наличие указанной роли у данного пользователя
   * @param roleEntity модель проверяемой роли
   * @return true, если пользователь имеет роль
   */
  public boolean hasRole(RoleEntity roleEntity) {

    return roleEntityList.contains(roleEntity);
  }
}
