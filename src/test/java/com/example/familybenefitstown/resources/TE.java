package com.example.familybenefitstown.resources;

import com.example.familybenefitstown.dto.entities.UserEntity;

import java.time.LocalDate;

/**
 * Предоставляет тестовые модели таблиц, Test Entities
 */
public class TE {

  public static final UserEntity UE_USER_ADMIN = UserEntity
      .builder()
      .id("id_user_admin")
      .email("user.admin@email.com")
      .name("UserAdmin")
      .dateBirth(LocalDate.of(2020, 10, 20))
      .build();

  public static final UserEntity UE_ADMIN = UserEntity
      .builder()
      .id("id_admin")
      .email("admin@email.com")
      .name("Admin")
      .build();

  public static final UserEntity UE_USER = UserEntity
      .builder()
      .id("id_user")
      .email("user@email.com")
      .name("User")
      .dateBirth(LocalDate.of(2020, 10, 20))
      .build();
}
