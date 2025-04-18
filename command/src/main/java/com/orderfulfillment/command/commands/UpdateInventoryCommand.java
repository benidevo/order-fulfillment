package com.orderfulfillment.command.commands;

import lombok.Builder;

/**
 * Command to update the quantity of a product in inventory.
 *
 * @param productId the unique identifier of the product
 * @param quantity the new available quantity
 */
@Builder
public record UpdateInventoryCommand(String productId, int quantity) {}
