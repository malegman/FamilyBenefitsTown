package com.example.familybenefitstown.dto.repository;

import com.example.familybenefitstown.dto.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Репозиторий, работающий с моделью таблицы "refresh_token"
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {

  /**
   * Возвращает модель токена восстановления
   * @param refreshToken токен восстановления
   * @return модель токена восстановления, или {@code empty}, если токен не найден
   */
  Optional<RefreshTokenEntity> findByToken(String refreshToken);

  /**
   * Удаляет модель токена восстановления по значению токена
   * @param refreshToken значение токена восстановления
   */
  @Transactional
  void deleteByToken(String refreshToken);
}
