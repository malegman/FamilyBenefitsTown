package com.example.familybenefitstown.security.service.impl;

import com.example.familybenefitstown.dto.entity.RoleEntity;
import com.example.familybenefitstown.resource.R;
import com.example.familybenefitstown.security.service.inface.TokenCodeService;
import com.example.familybenefitstown.security.web.auth.JwtAuthenticationUserData;
import com.example.familybenefitstown.service.inface.DateTimeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
   * Интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  private final DateTimeService dateTimeService;

  /**
   * Конструктор для инициализации сервиса
   * @param dateTimeService интерфейс сервиса, который предоставляет методы для работы с датой и временем
   */
  @Autowired
  public TokenCodeServiceFB(DateTimeService dateTimeService) {
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
        .setSubject(JwtAuthenticationUserData
                        .builder()
                        .idUser(id)
                        .nameRoleSet(roleEntitySet
                                         .stream()
                                         .map(RoleEntity::getName)
                                         .collect(Collectors.toSet()))
                        .ipAddress(request.getRemoteAddr())
                        .build()
                        .toString())
        .setExpiration(dateTimeService.getExpiration(R.JWT_EXPIRATION_SEC))
        .signWith(SignatureAlgorithm.HS512, R.JWT_SECRET)
        .compact();
  }

  /**
   * Генерирует jwt для пользователя на основе его ID, ролей и IP-адреса запроса на вход систему
   * @param userAuth данные доступа из токена доступа jwt
   * @return сгенерированный jwt
   */
  @Override
  public String generateJwt(JwtAuthenticationUserData userAuth) {
    return Jwts.builder()
        .setSubject(userAuth.toString())
        .setExpiration(dateTimeService.getExpiration(R.JWT_EXPIRATION_SEC))
        .signWith(SignatureAlgorithm.HS512, R.JWT_SECRET)
        .compact();
  }

  /**
   * Преобразует строковый токен в объект токена jwt
   * @param token конвертируемый строковый токен
   * @return токен в формате jwt
   * @throws RuntimeException если не удалось преобразовать токен
   */
  @Override
  public Jws<Claims> toJwt(String token) throws RuntimeException {

    return Jwts.parser().setSigningKey(R.JWT_SECRET).parseClaimsJws(token);
  }

  /**
   * Получает данные авторизации пользователя по токену формата jwt
   * @param jwt токен доступа пользователя в формате jwt
   * @return данные авторизации
   */
  @Override
  public JwtAuthenticationUserData authFromJwt(Jws<Claims> jwt) {

    return JwtAuthenticationUserData.fromString(jwt.getBody().getSubject());
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
