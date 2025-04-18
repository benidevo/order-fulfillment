package com.orderfulfillment.command.handlers;

import com.orderfulfillment.command.commands.AllocateInventoryCommand;
import com.orderfulfillment.command.commands.ReturnInventoryCommand;
import com.orderfulfillment.command.commands.UpdateInventoryCommand;

/**
 * Handles inventory-related commands by converting them to domain events.
 *
 * <p>This handler is responsible for processing commands related to inventory management and
 * publishing appropriate events to notify other parts of the system.
 */
public interface InventoryCommandHandler {

  /**
   * Handles a command to update inventory quantity.
   *
   * <p>Finds or creates the inventory for the specified product and updates its quantity.
   *
   * @param command the command containing the product ID and new quantity
   */
  void handle(UpdateInventoryCommand command);

  /**
   * Handles a command to allocate inventory to an order.
   *
   * <p>Allocates a specified quantity of a product to an order.
   *
   * @param command the command containing product ID, order ID, and quantity
   */
  void handle(AllocateInventoryCommand command);

  /**
   * Handles a command to return inventory from a cancelled order.
   *
   * <p>Returns previously allocated inventory back to available stock.
   *
   * @param command the command containing product ID, order ID, and quantity
   */
  void handle(ReturnInventoryCommand command);
}
