package com.orderfulfillment.command.exceptions.domain;

/**
 * Thrown when an attempt is made to cancel an order that is not in a cancellable state. This
 * indicates a violation of the business rule that only orders in certain states can be cancelled.
 */
public class OrderCannotBeCancelledException extends DomainRuleViolationException {
  private final String orderId;
  private final String currentStatus;

  public OrderCannotBeCancelledException(String orderId, String currentStatus) {
    super(String.format("Order %s in status %s cannot be cancelled", orderId, currentStatus));
    this.orderId = orderId;
    this.currentStatus = currentStatus;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }
}
