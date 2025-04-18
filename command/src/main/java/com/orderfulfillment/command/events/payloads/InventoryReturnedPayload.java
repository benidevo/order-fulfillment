package com.orderfulfillment.command.events.payloads;

/**
 * Represents the payload for an inventory return event.
 *
 * <p>This payload contains the product identifier, order identifier, and the returned quantity.
 *
 * @param productId the unique identifier of the product
 * @param orderId the unique identifier of the order
 * @param quantity the quantity returned from the order
 */
public record InventoryReturnedPayload(String productId, String orderId, int quantity) {}
