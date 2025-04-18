package com.orderfulfillment.command.commands;

import lombok.Builder;

/**
 * Command to return previously allocated inventory.
 *
 * @param productId the unique identifier of the product
 * @param orderId the unique identifier of the order
 * @param quantity the quantity to return
 */
@Builder
public record ReturnInventoryCommand(String productId, String orderId, int quantity) {}
