package com.orderfulfillment.command.commands;

import lombok.Builder;

/**
 * Command to allocate inventory to an order.
 *
 * @param productId the unique identifier of the product
 * @param orderId the unique identifier of the order
 * @param quantity the quantity to allocate
 */
@Builder
public record AllocateInventoryCommand(String productId, String orderId, int quantity) {}
