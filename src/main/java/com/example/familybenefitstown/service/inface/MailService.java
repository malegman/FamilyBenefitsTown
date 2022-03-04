package com.example.familybenefitstown.service.inface;

import org.springframework.mail.MailException;

/**
 * Сервис для отправки сообщений на электронную почту
 */
public interface MailService {

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
