package com.orderfulfillment.command.commands;

import com.orderfulfillment.command.domain.OrderStatus;

/**
 * Command to update the status of an existing order.
 *
 * <p>Encapsulates the order identifier and the target status. Can be constructed with an {@link
 * OrderStatus} enum or from its {@code String} name.
 *
 * @param orderId the unique identifier of the order to update
 * @param status the new {@link OrderStatus} to assign to the order
 */
public record UpdateOrderStatusCommand(String orderId, OrderStatus status) {
  public UpdateOrderStatusCommand(String orderId, String status) {
    this(orderId, OrderStatus.valueOf(status));
  }
}
