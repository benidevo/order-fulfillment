package com.orderfulfillment.command.commands;

/**
 * Represents a command to cancel an existing order.
 *
 * <p>Encapsulates the identifier of the order that needs to be canceled.
 *
 * @param orderId the unique identifier of the order to cancel
 */
public record CancelOrderCommand(String orderId) {}
