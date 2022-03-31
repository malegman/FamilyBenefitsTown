package com.example.familybenefitstown.exceptions.handle;

import com.example.familybenefitstown.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Обработчик исключений, выбрасываемых контроллерами
 */
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Обрабатывает исключение {@link AlreadyExistsException}, выброшенное контроллером
   * @param ex выброшенное контроллером исключение
   * @param request запрос, обработка которого вызывала исключение
   * @return ответ ошибки {@link ErrorResponse} со статусом ошибки и кодом варианта api
   */
  @ExceptionHandler(AlreadyExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException ex, WebRequest request) {

    HttpServletRequest httpServletRequest = ((HttpServletRequest)((NativeWebRequest)request).getNativeRequest());

    String requestURI = httpServletRequest.getRequestURI();
    String requestMethod = httpServletRequest.getMethod();
    String requestAddress = httpServletRequest.getRemoteAddr();

    log.warn("{} {} \"{}\": Already exists exception: {}", requestAddress, requestMethod, requestURI, ex.getMessage());

    return ResponseEntity.badRequest().body(new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(), AlreadyExistsException.API_VARIANT_CODE));
  }

  /**
   * Обрабатывает исключение {@link DateFormatException}, выброшенное контроллером
   * @param ex выброшенное контроллером исключение
   * @param request запрос, обработка которого вызывала исключение
   * @return ответ ошибки {@link ErrorResponse} со статусом ошибки и кодом варианта api
   */
  @ExceptionHandler(DateFormatException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleDateFormatException(DateFormatException ex, WebRequest request) {

    HttpServletRequest httpServletRequest = ((HttpServletRequest)((NativeWebRequest)request).getNativeRequest());

    String requestURI = httpServletRequest.getRequestURI();
    String requestMethod = httpServletRequest.getMethod();
    String requestAddress = httpServletRequest.getRemoteAddr();

    log.warn("{} {} \"{}\": Date format exception: {}", requestAddress, requestMethod, requestURI, ex.getMessage());

    return ResponseEntity.badRequest().body(new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(), DateFormatException.API_VARIANT_CODE));
  }

  /**
   * Обрабатывает исключение {@link DateTimeException}, выброшенное контроллером
   * @param ex выброшенное контроллером исключение
   * @param request запрос, обработка которого вызывала исключение
   * @return ответ ошибки {@link ErrorResponse} со статусом ошибки и кодом варианта api
   */
  @ExceptionHandler(DateTimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleDateTimeException(DateTimeException ex, WebRequest request) {

    HttpServletRequest httpServletRequest = ((HttpServletRequest)((NativeWebRequest)request).getNativeRequest());

    String requestURI = httpServletRequest.getRequestURI();
    String requestMethod = httpServletRequest.getMethod();
    String requestAddress = httpServletRequest.getRemoteAddr();

    log.warn("{} {} \"{}\": Date time exception: {}", requestAddress, requestMethod, requestURI, ex.getMessage());

    return ResponseEntity.badRequest().body(new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(), DateTimeException.API_VARIANT_CODE));
  }

  /**
   * Обрабатывает исключение {@link InvalidEmailException}, выброшенное контроллером
   * @param ex выброшенное контроллером исключение
   * @param request запрос, обработка которого вызывала исключение
   * @return ответ ошибки {@link ErrorResponse} со статусом ошибки и кодом варианта api
   */
  @ExceptionHandler(InvalidEmailException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleInvalidEmailException(InvalidEmailException ex, WebRequest request) {

    HttpServletRequest httpServletRequest = ((HttpServletRequest)((NativeWebRequest)request).getNativeRequest());

    String requestURI = httpServletRequest.getRequestURI();
    String requestMethod = httpServletRequest.getMethod();
    String requestAddress = httpServletRequest.getRemoteAddr();

    log.warn("{} {} \"{}\": Invalid email exception: {}", requestAddress, requestMethod, requestURI, ex.getMessage());

    return ResponseEntity.badRequest().body(new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(), InvalidEmailException.API_VARIANT_CODE));
  }

  /**
   * Обрабатывает исключение {@link InvalidStringException}, выброшенное контроллером
   * @param ex выброшенное контроллером исключение
   * @param request запрос, обработка которого вызывала исключение
   * @return ответ ошибки {@link ErrorResponse} со статусом ошибки и кодом варианта api
   */
  @ExceptionHandler(InvalidStringException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleInvalidStringException(InvalidStringException ex, WebRequest request) {

    HttpServletRequest httpServletRequest = ((HttpServletRequest)((NativeWebRequest)request).getNativeRequest());

    String requestURI = httpServletRequest.getRequestURI();
    String requestMethod = httpServletRequest.getMethod();
    String requestAddress = httpServletRequest.getRemoteAddr();

    log.warn("{} {} \"{}\": Invalid string exception: {}", requestAddress, requestMethod, requestURI, ex.getMessage());

    return ResponseEntity.badRequest().body(new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(), InvalidStringException.API_VARIANT_CODE));
  }
}
