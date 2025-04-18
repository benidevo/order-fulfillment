package com.orderfulfillment.command.exceptions.domain;

/**
 * Thrown when there is insufficient inventory for a product during order processing. This exception
 * indicates that the requested quantity exceeds the available stock for the specified product.
 */
public class InsufficientInventoryException extends DomainRuleViolationException {
  private final String productId;
  private final int requested;
  private final int available;

  public InsufficientInventoryException(String productId, int requested, int available) {
    super(
        String.format(
            "Insufficient inventory for product %s: requested %d, available %d",
            productId, requested, available));
    this.productId = productId;
    this.requested = requested;
    this.available = available;
  }

  public String getProductId() {
    return productId;
  }

  public int getRequested() {
    return requested;
  }

  public int getAvailable() {
    return available;
  }
}
