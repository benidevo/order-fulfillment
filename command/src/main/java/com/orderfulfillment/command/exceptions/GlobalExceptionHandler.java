package com.orderfulfillment.command.exceptions;

import com.orderfulfillment.command.api.dtos.ErrorResponseDto;
import com.orderfulfillment.command.api.dtos.ValidationErrorDto;
import com.orderfulfillment.command.exceptions.domain.CancelledOrderModificationException;
import com.orderfulfillment.command.exceptions.domain.DomainRuleViolationException;
import com.orderfulfillment.command.exceptions.domain.InvalidOrderStatusTransitionException;
import com.orderfulfillment.command.exceptions.domain.OrderCannotBeCancelledException;
import com.orderfulfillment.command.exceptions.domain.OrderFulfillmentException;
import com.orderfulfillment.command.exceptions.domain.OrderNotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
   * Handles order not found exceptions.
   *
   * <p>This method captures cases where an order is not found in the system, logs the error
   * message, and constructs a response entity indicating the order ID that was not found.
   *
   * @param ex the exception indicating an order was not found
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating the order not
   *     found
   */
  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleOrderNotFoundException(OrderNotFoundException ex) {
    log.warn("Order not found: {}", ex.getMessage());
    Map<String, Object> details = new HashMap<>();
    details.put("orderId", ex.getOrderId());

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("ORDER_NOT_FOUND")
            .message(ex.getMessage())
            .details(details)
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  /**
   * Handles invalid order status transition exceptions.
   *
   * <p>This method captures cases where an invalid status transition is attempted on an order, logs
   * the error message, and constructs a response entity indicating the details of the invalid
   * transition.
   *
   * @param ex the exception indicating an invalid order status transition
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating the invalid
   *     transition
   */
  @ExceptionHandler(InvalidOrderStatusTransitionException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidStatusTransition(
      InvalidOrderStatusTransitionException ex) {
    log.warn("Invalid status transition: {}", ex.getMessage());
    Map<String, Object> details = new HashMap<>();
    details.put("orderId", ex.getOrderId());
    details.put("currentStatus", ex.getCurrentStatus());
    details.put("targetStatus", ex.getTargetStatus());

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("INVALID_STATUS_TRANSITION")
            .message(ex.getMessage())
            .details(details)
            .build();

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
  }

  /**
   * Handles cancelled order modification exceptions.
   *
   * <p>This method captures cases where an attempt is made to modify a cancelled order, logs the
   * error message, and constructs a response entity indicating the order ID that was attempted to
   * be modified.
   *
   * @param ex the exception indicating a cancelled order modification
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating the cancelled
   *     order
   */
  @ExceptionHandler(CancelledOrderModificationException.class)
  public ResponseEntity<ErrorResponseDto> handleCancelledOrderModification(
      CancelledOrderModificationException ex) {
    log.warn("Attempt to modify cancelled order: {}", ex.getMessage());
    Map<String, Object> details = new HashMap<>();
    details.put("orderId", ex.getOrderId());

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("CANCELLED_ORDER_MODIFICATION")
            .message(ex.getMessage())
            .details(details)
            .build();

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
  }

  /**
   * Handles order cannot be cancelled exceptions.
   *
   * <p>This method captures cases where an attempt is made to cancel an order that cannot be
   * cancelled, logs the error message, and constructs a response entity indicating the order ID and
   * current status of the order.
   *
   * @param ex the exception indicating an order cannot be cancelled
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating the order cannot
   *     be cancelled
   */
  @ExceptionHandler(OrderCannotBeCancelledException.class)
  public ResponseEntity<ErrorResponseDto> handleOrderCannotBeCancelled(
      OrderCannotBeCancelledException ex) {
    log.warn("Attempt to cancel an order that cannot be cancelled: {}", ex.getMessage());
    Map<String, Object> details = new HashMap<>();
    details.put("orderId", ex.getOrderId());
    details.put("currentStatus", ex.getCurrentStatus());

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("ORDER_CANNOT_BE_CANCELLED")
            .message(ex.getMessage())
            .details(details)
            .build();

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
  }

  /**
   * Handles concurrency exceptions.
   *
   * <p>This method captures cases where a concurrency conflict occurs, logs the error message, and
   * constructs a response entity indicating the details of the conflict.
   *
   * @param ex the exception indicating a concurrency conflict
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating the concurrency
   *     conflict
   */
  @ExceptionHandler(ConcurrencyException.class)
  public ResponseEntity<ErrorResponseDto> handleConcurrencyException(ConcurrencyException ex) {
    log.warn("Concurrency conflict: {}", ex.getMessage());
    Map<String, Object> details = new HashMap<>();
    details.put("aggregateId", ex.getAggregateId());
    details.put("expectedVersion", ex.getExpectedVersion());
    details.put("actualVersion", ex.getActualVersion());

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("CONCURRENCY_CONFLICT")
            .message(ex.getMessage())
            .details(details)
            .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  /**
   * Handles event publishing exceptions.
   *
   * <p>This method captures cases where an error occurs while publishing events, logs the error
   * message, and constructs a response entity indicating the details of the event that failed to be
   * published.
   *
   * @param ex the exception indicating an event publishing error
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating the event
   *     publishing error
   */
  @ExceptionHandler(EventPublishingException.class)
  public ResponseEntity<ErrorResponseDto> handleEventPublishingException(
      EventPublishingException ex) {
    log.error("Event publishing error: {}", ex.getMessage(), ex);
    Map<String, Object> details = new HashMap<>();
    details.put("eventId", ex.getEventId());

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("EVENT_PUBLISHING_ERROR")
            .message("An error occurred while publishing events")
            .details(details)
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * Handles domain rule violation exceptions.
   *
   * <p>This method captures cases where a domain rule is violated, logs the error message, and
   * constructs a response entity indicating the details of the violation.
   *
   * @param ex the exception indicating a domain rule violation
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating the domain rule
   *     violation
   */
  @ExceptionHandler(DomainRuleViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleDomainRuleViolation(
      DomainRuleViolationException ex) {
    log.warn("Domain rule violation: {}", ex.getMessage());

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("DOMAIN_RULE_VIOLATION")
            .message(ex.getMessage())
            .details(new HashMap<>())
            .build();

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
  }

  /**
   * Handles order fulfillment exceptions.
   *
   * <p>This method captures cases where an error occurs during order fulfillment, logs the error
   * message, and constructs a response entity indicating the details of the error.
   *
   * @param ex the exception indicating an order fulfillment error
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating the order
   *     fulfillment error
   */
  @ExceptionHandler(OrderFulfillmentException.class)
  public ResponseEntity<ErrorResponseDto> handleOrderFulfillmentException(
      OrderFulfillmentException ex) {
    log.error("Order fulfillment error: {}", ex.getMessage());

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("ORDER_FULFILLMENT_ERROR")
            .message(ex.getMessage())
            .details(new HashMap<>())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * Handles general exceptions.
   *
   * <p>This method captures any unhandled exceptions, logs the error message, and constructs a
   * response entity indicating an internal server error.
   *
   * @param ex the exception indicating an internal server error
   * @return a {@link ResponseEntity} with an {@link ErrorResponseDto} indicating an internal server
   *     error
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
    log.error("Internal server error: {}", ex.getMessage(), ex);

    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .success(false)
            .error("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred")
            .details(new HashMap<>())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
