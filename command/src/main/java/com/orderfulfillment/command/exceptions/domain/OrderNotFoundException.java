package com.orderfulfillment.command.exceptions.domain;

/**
 * Thrown when an order is not found in the system. This exception indicates that the requested
 * order ID does not exist in the database or order repository.
 */
public class OrderNotFoundException extends OrderFulfillmentException {
  private final String orderId;

  public OrderNotFoundException(String orderId) {
    super("Order not found: " + orderId);
    this.orderId = orderId;
  }

  public String getOrderId() {
    return orderId;
  }
}
