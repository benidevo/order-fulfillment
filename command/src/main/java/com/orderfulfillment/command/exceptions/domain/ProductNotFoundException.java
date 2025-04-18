package com.orderfulfillment.command.exceptions.domain;

/**
 * Thrown when a product is not found in the inventory. This indicates that the requested product ID
 * does not exist in the inventory system.
 */
public class ProductNotFoundException extends OrderFulfillmentException {
  private final String productId;

  public ProductNotFoundException(String productId) {
    super("Product not found in inventory: " + productId);
    this.productId = productId;
  }

  public String getProductId() {
    return productId;
  }
}
