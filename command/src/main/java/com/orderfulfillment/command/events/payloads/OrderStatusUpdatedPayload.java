package com.orderfulfillment.command.events.payloads;

import com.orderfulfillment.command.domain.OrderStatus;

/**
 * Represents the payload for an order status update event.
 *
 * <p>This payload contains the unique identifier of the order and its new status.
 *
 * @param orderId the unique identifier of the order
 * @param status the new status of the order
 */
public record OrderStatusUpdatedPayload(String orderId, OrderStatus status) {}
