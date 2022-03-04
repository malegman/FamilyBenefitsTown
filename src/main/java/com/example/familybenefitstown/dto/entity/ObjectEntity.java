package com.example.familybenefitstown.dto.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * Базовый класс для моделей таблиц
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public abstract class ObjectEntity {

  /**
   * ID объекта
   */
  private String id;

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    ObjectEntity objectEntity = (ObjectEntity) o;
    return Objects.equals(id, objectEntity.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
