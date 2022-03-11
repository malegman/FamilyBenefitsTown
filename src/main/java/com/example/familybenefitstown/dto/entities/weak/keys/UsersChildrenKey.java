package com.example.familybenefitstown.dto.entities.weak.keys;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Ключ записи таблицы "users_children"
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode(of = {"idUser", "idChild"})
public class UsersChildrenKey implements Serializable {

  @Column(name = "id_user")
  private String idUser;

  @Column(name = "id_child")
  private String idChild;
}
