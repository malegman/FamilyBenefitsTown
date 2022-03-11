package com.example.familybenefitstown.dto.entities.weak.keys;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Ключ записи таблицы "users_roles"
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode(of = {"idUser", "idRole"})
public class UsersRolesKey implements Serializable {

  @Column(name = "id_user")
  private String idUser;

  @Column(name = "id_role")
  private String idRole;
}
