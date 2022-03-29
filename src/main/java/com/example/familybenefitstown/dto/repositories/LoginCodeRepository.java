package com.example.familybenefitstown.dto.repositories;

import com.example.familybenefitstown.dto.entities.LoginCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

/**
 * Репозиторий, работающий с моделью таблицы "login_code"
 */
public interface LoginCodeRepository extends JpaRepository<LoginCodeEntity, String> {

  /**
   * Проверяет наличие модели кода для входа по его значению
   * @param loginCode значение кода для входа
   * @return true, если модель с указанным кодом существует
   */
  boolean existsByCode(int loginCode);

  /**
   * Возвращает модель кода для входа по значению кода пользователя
   * @param code пользовательский код для входа в систему
   * @return модель кода для входа или {@code empty}, если модель не найдена
   */
  Optional<LoginCodeEntity> findByCode(int code);

  /**
   * Удаляет код для входа по его значению
   * @param loginCode код для входа
   */
  @Modifying
  void deleteByCode(int loginCode);
}
