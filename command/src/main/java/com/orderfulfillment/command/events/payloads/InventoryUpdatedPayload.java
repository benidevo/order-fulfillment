package com.orderfulfillment.command.events.payloads;

/**
 * Represents the payload for an inventory update event.
 *
 * <p>This payload contains the product identifier and the new quantity.
 *
 * @param productId the unique identifier of the product
 * @param quantity the new available quantity
 */
public record InventoryUpdatedPayload(String productId, int quantity) {}
