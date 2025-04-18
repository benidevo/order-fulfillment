package com.orderfulfillment.command.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents an item in an order.
 *
 * <p>Encapsulates the product identifier, quantity, and price of the item.
 *
 * @param productId the unique identifier of the product
 * @param quantity the quantity of the product ordered
 * @param price the price of the product
 */
@Getter
@ToString
@EqualsAndHashCode
public class OrderItem {
  private final String productId;
  private final int quantity;
  private final Money price;

  public OrderItem(String productId, int quantity, Money price) {
    validateProductId(productId);
    validateQuantity(quantity);

    this.productId = productId;
    this.quantity = quantity;
    this.price = price;
  }

  private void validateQuantity(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
  }

  private void validateProductId(String productId) {
    if (productId == null || productId.isBlank()) {
      throw new IllegalArgumentException("Product ID is required");
    }
  }

  public Money calculateTotal() {
    return price.multiply(quantity);
  }
}
