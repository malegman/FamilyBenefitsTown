package com.example.familybenefitstown.dto.repository;

import com.example.familybenefitstown.dto.entity.LoginCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий, работающий с моделью таблицы "login_code"
 */
public interface LoginCodeRepository extends JpaRepository<LoginCodeEntity, String> {
}
