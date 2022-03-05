package com.example.familybenefitstown.security.service.impl;

import com.example.familybenefitstown.dto.entity.RoleEntity;
import com.example.familybenefitstown.dto.repository.AccessTokenRepository;
import com.example.familybenefitstown.resource.R;
import com.example.familybenefitstown.security.service.inface.TokenCodeService;
import com.example.familybenefitstown.security.web.auth.JwtUserData;
import com.example.familybenefitstown.service.inface.DateTimeService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с токеном доступа (в формате jwt) и кодом для входа
 */
@Service
public class TokenCodeServiceFB implements TokenCodeService {

  /**
   * Репозиторий, работающий с моделью таблицы "access_token"
   */
  private final AccessTokenRepository accessTokenRepository;

  /**
   * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  private final DateTimeService dateTimeService;

  /**
   * Конструктор для инициализации сервиса
   * @param accessTokenRepository репозиторий, работающий с моделью таблицы "access_token"
   * @param dateTimeService интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  @Autowired
  public TokenCodeServiceFB(AccessTokenRepository accessTokenRepository,
                            DateTimeService dateTimeService) {
    this.accessTokenRepository = accessTokenRepository;
    this.dateTimeService = dateTimeService;
  }

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param id ID пользователя
   * @param roleEntitySet множество ролей пользователя
   * @param request http запрос на вход систему
   * @return сгенерированный jwt
   */
  @Override
  public String generateJwt(String id, Set<RoleEntity> roleEntitySet, HttpServletRequest request) {

    return Jwts.builder()
        .setSubject(JwtUserData
                        .builder()
                        .idUser(id)
                        .nameRoleSet(roleEntitySet
                                         .stream()
                                         .map(RoleEntity::getName)
                                         .collect(Collectors.toSet()))
                        .address(request.getRemoteAddr())
                        .build()
                        .toString())
        .setExpiration(dateTimeService.getExpiration(R.JWT_EXPIRATION_SEC))
        .signWith(SignatureAlgorithm.HS512, R.JWT_SECRET)
        .compact();
  }

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param userData данные доступа из токена доступа jwt
   * @return сгенерированный jwt
   */
  @Override
  public String generateJwt(JwtUserData userData) {
    return Jwts.builder()
        .setSubject(userData.toString())
        .setExpiration(dateTimeService.getExpiration(R.JWT_EXPIRATION_SEC))
        .signWith(SignatureAlgorithm.HS512, R.JWT_SECRET)
        .compact();
  }

  /**
   * Извлекает данные пользователя из строки, формата токена jwt
   * @param jwt токен пользователя, jwt
   * @return данные авторизации
   * @throws RuntimeException если не удалось извлечь данные пользователя из строки
   */
  @Override
  public JwtUserData authFromStringJwt(String jwt) throws RuntimeException {

    return JwtUserData.fromString(
        Jwts.parser().setSigningKey(R.JWT_SECRET)
            .parseClaimsJws(jwt).getBody().getSubject());
  }

  /**
   * Проверяет наличие в бд токена jwt по ID данного пользователя.
   * Таким образом проверяется, был ли осуществлен выход пользователя из системы.
   * @param idUser ID пользователя
   * @return true, если токен jwt данного пользователя есть в бд
   */
  @Override
  public boolean existsJwtById(String idUser) {

    return accessTokenRepository.existsById(idUser);
  }

  /**
   * Создает код для входа в систему.
   * Код представляет собой 6-ти значное число
   * @return сгенерированный код
   */
  @Override
  public int generateLoginCode() {

    byte[] randBytes = new byte[R.LOGIN_CODE_LENGTH];
    int loginCode = 0;

    // Для пропорционального приведения диапазона [0-255] к диапазону [0-9]
    double part = 10 / 255.0;

    // Получение случайных значений
    (new SecureRandom()).nextBytes(randBytes);

    // "Заполнение" числа цифрами
    for (int randI = 0, tempVal = 1; randI < R.LOGIN_CODE_LENGTH; randI++, tempVal *= 10) {
      loginCode += tempVal * (int) Math.floor((randBytes[randI] + 127) * part);
    }

    return loginCode;
  }
}
