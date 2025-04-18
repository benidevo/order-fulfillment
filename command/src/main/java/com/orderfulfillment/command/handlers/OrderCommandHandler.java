package com.orderfulfillment.command.handlers;

import com.orderfulfillment.command.commands.CancelOrderCommand;
import com.orderfulfillment.command.commands.CreateOrderCommand;
import com.orderfulfillment.command.commands.UpdateOrderStatusCommand;

/**
 * Handles order-related commands by converting them to domain events.
 *
 * <p>This handler is responsible for processing commands related to order lifecycle and publishing
 * appropriate events to notify other parts of the system.
 */
public interface OrderCommandHandler {

  /**
   * Handles a command to create a new order.
   *
   * <p>Converts the create order command into an OrderCreatedEvent with the appropriate payload
   * containing customer details, order items, addresses, and total cost. The order is initially set
   * to the REGISTERED status.
   *
   * @param command the command containing all required order creation information
   */
  void handle(CreateOrderCommand command);

  /**
   * Handles a command to update an order's status.
   *
   * <p>Converts the status update command into an OrderStatusUpdatedEvent with the appropriate
   * payload containing the order ID and the new status.
   *
   * @param command the command containing the order ID and the new status
   */
  void handle(UpdateOrderStatusCommand command);

  /**
   * Handles a command to cancel an order.
   *
   * <p>Converts the cancellation command into an OrderCancelledEvent with the appropriate payload
   * containing the order ID.
   *
   * @param command the command containing the order ID to be cancelled
   */
  void handle(CancelOrderCommand command);
}
