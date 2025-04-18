package com.orderfulfillment.command.exceptions.domain;

/**
 * Thrown when attempting to modify an order that has already been cancelled. This indicates a
 * violation of the business rule that cancelled orders cannot be modified.
 */
public class CancelledOrderModificationException extends DomainRuleViolationException {
  private final String orderId;

  public CancelledOrderModificationException(String orderId) {
    super("Cannot modify a cancelled order: " + orderId);
    this.orderId = orderId;
  }

  public String getOrderId() {
    return orderId;
  }
}
