package com.example.familybenefitstown.dto.repository;

import com.example.familybenefitstown.dto.entity.AccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий, работающий с моделью таблицы "access_token"
 */
public interface AccessTokenRepository extends JpaRepository<AccessTokenEntity, String> {
}
