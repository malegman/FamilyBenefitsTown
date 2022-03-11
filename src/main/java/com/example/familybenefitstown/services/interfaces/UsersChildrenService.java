package com.example.familybenefitstown.services.interfaces;

import com.example.familybenefitstown.dto.entities.strong.ChildEntity;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс сервиса, управляющего связью пользователей и детей
 */
public interface UsersChildrenService {

  /**
   * Создает связь между пользователем и данным ребенком, добавляет пользователю ребенка
   * @param userEntity модель пользователя, которому добавляется ребенок
   * @param childEntity модель добавляемого ребенка
   */
  void addUserChild(UserEntity userEntity, ChildEntity childEntity);

  /**
   * Создает связь между пользователем и ребенком с указанной датой рождения, добавляет пользователю ребенка
   * @param userEntity  модель пользователя, которому добавляется ребенок
   * @param childBirth дата рождения добавляемого ребенка
   */
  void addUserChild(UserEntity userEntity, LocalDate childBirth);

  /**
   * Возвращает список детей указанного пользователя
   * @param userEntity модель пользователя
   * @return список детей пользователя
   */
  List<ChildEntity> getChildrenByUser(UserEntity userEntity);
}
