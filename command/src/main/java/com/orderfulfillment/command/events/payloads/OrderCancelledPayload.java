package com.orderfulfillment.command.events.payloads;

/**
 * Represents the payload for an order cancellation event.
 *
 * <p>This payload contains the unique identifier of the order that has been cancelled.
 *
 * @param orderId the unique identifier of the cancelled order
 */
public record OrderCancelledPayload(String orderId) {}
