package com.orderfulfillment.command.exceptions;

import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.orderfulfillment.command.dtos.ValidationErrorDto;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorDto> handleValidationException(
      MethodArgumentNotValidException ex) {
    log.warn("Validation error: {}", ex.getMessage());

    HashMap<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              var fieldName = ((FieldError) error).getField();
              errors.put(fieldName, error.getDefaultMessage());
            });
    ValidationErrorDto response =
        ValidationErrorDto.builder().success(false).errors(errors).build();
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ValidationErrorDto> handleMissingParams(
      MissingServletRequestParameterException ex) {
    log.warn("Missing parameter: {}", ex.getMessage());
    HashMap<String, String> errors = new HashMap<>();
    errors.put(ex.getParameterName(), "Parameter is required");
    ValidationErrorDto response =
        ValidationErrorDto.builder().success(false).errors(errors).build();
    return ResponseEntity.badRequest().body(response);
  }
}
