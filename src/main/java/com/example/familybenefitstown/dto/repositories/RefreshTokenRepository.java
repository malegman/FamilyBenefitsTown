package com.example.familybenefitstown.dto.repositories;

import com.example.familybenefitstown.dto.entities.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

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
  @Modifying
  void deleteByToken(String refreshToken);
}
