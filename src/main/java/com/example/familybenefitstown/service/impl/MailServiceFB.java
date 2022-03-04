package com.example.familybenefitstown.service.impl;

import com.example.familybenefitstown.resource.RMail;
import com.example.familybenefitstown.service.inface.MailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Реализация сервиса для отправки сообщений на электронную почту
 */
@Service
public class MailServiceFB implements MailService {

  /**
   * Почтовый сервис
   */
  private static final JavaMailSenderImpl mailSender;

  // инициализация и настройка почтового сервиса
  static {
    mailSender = new JavaMailSenderImpl();

    mailSender.setHost(RMail.HOST);
    mailSender.setPort(RMail.PORT);
    mailSender.setUsername(RMail.USERNAME);
    mailSender.setPassword(RMail.PASSWORD);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");
  }

  /**
   * Отправляет сообщение с кодом для входа указанному пользователю
   * @param to адрес получателя, email пользователя
   * @param nameUser имя пользователя
   * @param loginCode код для входа в систему
   * @throws MailException если не удалось отправить сообщение
   */
  @Override
  public void sendLoginCode(String to, String nameUser, int loginCode) throws MailException {

    send(to, RMail.LOGIN_MESSAGE_SUBJECT, String.format(RMail.LOGIN_MESSAGE_TEXT_PATTERN, nameUser, loginCode));
  }

  /**
   * Отправляет сообщение от имени сервиса пользователю по адресу с темой и текстом
   * @param to адрес получателя
   * @param subject тема сообщения
   * @param text текст сообщения
   * @throws MailException если не удалось отправить сообщение
   */
  @Override
  public void send(String to, String subject, String text) throws MailException {

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);

    mailSender.send(message);
  }
}

