package com.orderfulfillment.command.events.payloads;

/**
 * Represents the payload for an inventory allocation event.
 *
 * <p>This payload contains the product identifier, order identifier, and the allocated quantity.
 *
 * @param productId the unique identifier of the product
 * @param orderId the unique identifier of the order
 * @param quantity the quantity allocated to the order
 */
public record InventoryAllocatedPayload(String productId, String orderId, int quantity) {}
