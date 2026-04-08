package com.brunpola.cv_management.exceptions;

import com.brunpola.cv_management.exceptions.base.NotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application.
 *
 * <p>Uses {@code @ControllerAdvice} to intercept exceptions thrown by controllers and return
 * consistent HTTP responses.
 *
 * <p>All responses follow a common structure:
 *
 * <ul>
 *   <li>{@code timestamp} – time of the error
 *   <li>{@code status} – HTTP status code
 *   <li>{@code error} – HTTP reason phrase
 *   <li>{@code message} – error details
 * </ul>
 */
@ControllerAdvice
@Log
public class GlobalExceptionHandler {

  /**
   * Builds a standardized error response body.
   *
   * @param status HTTP status to return
   * @param message error message or details
   * @return response entity containing structured error information
   */
  private ResponseEntity<Object> build(HttpStatus status, Object message) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles all {@link NotFoundException} instances.
   *
   * @param ex thrown exception
   * @return HTTP 404 response with error message
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  /**
   * Handles validation errors triggered by {@link MethodArgumentNotValidException}.
   *
   * <p>Extracts field-level validation messages and returns them as a map where keys are field
   * names and values are error messages.
   *
   * @param ex validation exception
   * @return HTTP 400 response with validation error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
    ResponseEntity<Object> responseEntity = build(HttpStatus.BAD_REQUEST, errors);

    log.info("MOJ LOG @Log: " + responseEntity.getBody());
    return responseEntity;
  }
}
