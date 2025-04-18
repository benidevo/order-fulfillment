package com.orderfulfillment.command.exceptions.domain;

/**
 * Base exception for all domain-specific exceptions in the order fulfillment system. Serves as a
 * common type for catching domain exceptions.
 */
public abstract class OrderFulfillmentException extends RuntimeException {
  public OrderFulfillmentException(String message) {
    super(message);
  }

  public OrderFulfillmentException(String message, Throwable cause) {
    super(message, cause);
  }
}
