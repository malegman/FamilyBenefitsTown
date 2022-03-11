package com.example.familybenefitstown.services.interfaces;

import com.example.familybenefitstown.exceptions.InvalidEmailException;
import org.springframework.mail.MailException;

/**
 * Интерфейс сервиса для отправки сообщений на электронную почту
 */
public interface MailService {

  /**
   * Проверяет корректность email
   * @param email проверяемый email
   * @throws InvalidEmailException если указанный "email" не является email
   */
  void checkEmailElseThrow(String email) throws InvalidEmailException;

  /**
   * Отправляет сообщение от имени сервиса пользователю по адресу с темой и текстом
   * @param to адрес получателя, email пользователя
   * @param subject тема сообщения
   * @param text текст сообщения
   * @throws MailException если не удалось отправить сообщение
   */
  void send(String to, String subject, String text) throws MailException;

  /**
   * Отправляет сообщение с кодом для входа указанному пользователю
   * @param to адрес получателя, email пользователя
   * @param nameUser имя пользователя
   * @param loginCode код для входа в систему
   * @throws MailException если не удалось отправить сообщение
   */
  void sendLoginCode(String to, String nameUser, int loginCode) throws MailException;
}
