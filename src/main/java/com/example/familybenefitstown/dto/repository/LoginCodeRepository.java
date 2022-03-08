package com.example.familybenefitstown.dto.repository;

import com.example.familybenefitstown.dto.entity.LoginCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий, работающий с моделью таблицы "login_code"
 */
public interface LoginCodeRepository extends JpaRepository<LoginCodeEntity, String> {

  /**
   * Возвращает модель кода для входа по значению кода пользователя
   * @param code пользовательский код для входа в систему
   * @return модель кода для входа или {@code empty}, если модель не найдена
   */
  Optional<LoginCodeEntity> findByCode(int code);
}
