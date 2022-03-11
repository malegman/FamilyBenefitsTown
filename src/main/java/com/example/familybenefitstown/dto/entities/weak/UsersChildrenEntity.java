package com.example.familybenefitstown.dto.entities.weak;

import com.example.familybenefitstown.dto.entities.strong.ChildEntity;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.dto.entities.weak.keys.UsersChildrenKey;
import lombok.*;

import javax.persistence.*;

/**
 * Модель записи таблицы "users_children"
 */
@Entity
@Table(name = "users_children", schema = "family_benefit_town")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class UsersChildrenEntity {

  @EmbeddedId
  private UsersChildrenKey id;

  @ManyToOne
  @MapsId("idUser")
  @JoinColumn(name = "id_user")
  private UserEntity userEntity;

  @ManyToOne
  @MapsId("idChild")
  @JoinColumn(name = "id_child")
  private ChildEntity childEntity;
}
