package com.orderfulfillment.command.exceptions;

/**
 * Exception thrown when a concurrency conflict occurs during the save operation of an Order
 * aggregate.
 *
 * <p>This exception is typically used to indicate that the version of the Order being saved does not
 * match the expected version in the event store, preventing the save operation from completing.
 */
public class ConcurrencyException extends RuntimeException {

  public ConcurrencyException(String message) {
    super(message);
  }

  public ConcurrencyException(String message, Throwable cause) {
    super(message, cause);
  }
}
