package com.example.familybenefitstown.services.implementations;

import com.example.familybenefitstown.dto.entities.strong.ChildEntity;
import com.example.familybenefitstown.dto.entities.strong.UserEntity;
import com.example.familybenefitstown.dto.entities.weak.UsersChildrenEntity;
import com.example.familybenefitstown.dto.entities.weak.keys.UsersChildrenKey;
import com.example.familybenefitstown.dto.repositories.strong.ChildRepository;
import com.example.familybenefitstown.dto.repositories.weak.UsersChildrenRepository;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.security.generator.RandomValue;
import com.example.familybenefitstown.services.interfaces.UsersChildrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса, управляющего связью пользователей и детей
 */
@Service
public class UsersChildrenServiceFB implements UsersChildrenService {

  /**
   * Репозиторий, работающий с моделью таблицы "users_children"
   */
  private final UsersChildrenRepository usersChildrenRepository;
  /**
   * Репозиторий, работающий с моделью таблицы "child"
   */
  private final ChildRepository childRepository;

  /**
   * Конструктор для инициализации репозитория
   * @param usersChildrenRepository репозиторий, работающий с моделью таблицы "users_children"
   * @param childRepository репозиторий, работающий с моделью таблицы "child"
   */
  @Autowired
  public UsersChildrenServiceFB(UsersChildrenRepository usersChildrenRepository, ChildRepository childRepository) {
    this.usersChildrenRepository = usersChildrenRepository;
    this.childRepository = childRepository;
  }

  /**
   * Создает связь между пользователем и данным ребенком, добавляет пользователю ребенка
   * @param userEntity  модель пользователя, которому добавляется ребенок
   * @param childEntity модель добавляемого ребенка
   */
  @Override
  public void addUserChild(UserEntity userEntity, ChildEntity childEntity) {

    usersChildrenRepository.saveAndFlush(UsersChildrenEntity
                                             .builder()
                                             .id(new UsersChildrenKey(userEntity.getId(), childEntity.getId()))
                                             .userEntity(userEntity)
                                             .childEntity(childEntity)
                                             .build());

  }

  /**
   * Создает связь между пользователем и ребенком с указанной датой рождения, добавляет пользователю ребенка
   * @param userEntity  модель пользователя, которому добавляется ребенок
   * @param childBirth дата рождения добавляемого ребенка
   */
  @Override
  public void addUserChild(UserEntity userEntity, LocalDate childBirth) {

    ChildEntity childEntityFromRequest = childRepository.findByDateBirth(childBirth)
        .orElse(new ChildEntity(RandomValue.randomString(R.ID_LENGTH), childBirth));

    addUserChild(userEntity, childEntityFromRequest);
  }

  /**
   * Возвращает список детей указанного пользователя
   * @param userEntity модель пользователя
   * @return список детей пользователя
   */
  @Override
  public List<ChildEntity> getChildrenByUser(UserEntity userEntity) {

    return usersChildrenRepository.findAllByUserEntity(userEntity)
        .stream()
        .map(UsersChildrenEntity::getChildEntity)
        .collect(Collectors.toList());
  }
}
