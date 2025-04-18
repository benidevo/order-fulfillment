package com.orderfulfillment.command.exceptions.domain;

/**
 * Thrown when an invalid status transition is attempted for an order. This exception indicates that
 * the current status of the order does not allow for the requested transition to the target status.
 */
public class InvalidOrderStatusTransitionException extends DomainRuleViolationException {
  private final String orderId;
  private final String currentStatus;
  private final String targetStatus;

  public InvalidOrderStatusTransitionException(
      String orderId, String currentStatus, String targetStatus) {
    super(
        String.format(
            "Invalid status transition for order %s from %s to %s",
            orderId, currentStatus, targetStatus));
    this.orderId = orderId;
    this.currentStatus = currentStatus;
    this.targetStatus = targetStatus;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public String getTargetStatus() {
    return targetStatus;
  }
}
