package com.orderfulfillment.command.exceptions;

import com.orderfulfillment.command.api.dtos.ValidationErrorDto;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  /**
   * Handles validation exceptions thrown by the Spring framework.
   *
   * <p>This method captures validation errors from incoming requests, logs the error message, and
   * constructs a response entity containing the validation errors in a structured format.
   *
   * @param ex the exception containing validation errors
   * @return a {@link ResponseEntity} with a {@link ValidationErrorDto} containing the errors
   */
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

  /**
   * Handles missing servlet request parameter exceptions.
   *
   * <p>This method captures cases where required parameters are missing from the request, logs the
   * error message, and constructs a response entity indicating the missing parameter.
   *
   * @param ex the exception indicating a missing request parameter
   * @return a {@link ResponseEntity} with a {@link ValidationErrorDto} indicating the missing
   *     parameter
   */
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

  /**
   * Handles any other exceptions that may occur in the application.
   *
   * <p>This method captures general exceptions, logs the error message, and constructs a response
   * entity indicating an internal server error.
   *
   * @param ex the exception that occurred
   * @return a {@link ResponseEntity} indicating an internal server error
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ValidationErrorDto> handleGeneralException(Exception ex) {
    log.error("Internal server error: {}", ex.getMessage());
    ValidationErrorDto response =
        ValidationErrorDto.builder().success(false).errors(new HashMap<>()).build();
    return ResponseEntity.internalServerError().body(response);
  }
}
