package com.example.familybenefitstown.dto.entities.weak;

import com.example.familybenefitstown.dto.entities.strong.RoleEntity;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.dto.entities.weak.keys.UsersRolesKey;
import lombok.*;

import javax.persistence.*;

/**
 * Модель записи таблицы "users_roles"
 */
@Entity
@Table(name = "users_roles", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class UsersRolesEntity {

  @EmbeddedId
  private UsersRolesKey id;

  @ManyToOne
  @MapsId("idUser")
  @JoinColumn(name = "id_user")
  private UserEntity userEntity;

  @ManyToOne
  @MapsId("idRole")
  @JoinColumn(name = "id_role")
  private RoleEntity roleEntity;
}
