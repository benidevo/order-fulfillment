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
   * <p>Converts the cancel order command into an OrderCancelledEvent. This process involves finding
   * the existing order by ID, applying domain-specific cancellation rules, and persisting the
   * cancellation event if allowed by business rules.
   *
   * <p>An order can only be cancelled if it's in certain states (e.g., not already shipped or
   * delivered).
   *
   * @param command the command containing the order ID to be cancelled
   * @throws com.orderfulfillment.command.exceptions.domain.OrderNotFoundException if the specified
   *     order doesn't exist
   * @throws com.orderfulfillment.command.exceptions.domain.OrderCannotBeCancelledException if
   *     business rules prevent the order from being cancelled
   */
  void handle(CancelOrderCommand command);
}
