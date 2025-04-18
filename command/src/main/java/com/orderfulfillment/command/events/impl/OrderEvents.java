package com.orderfulfillment.command.events.impl;

import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.events.payloads.OrderCancelledPayload;
import com.orderfulfillment.command.events.payloads.OrderCreatedPayload;
import com.orderfulfillment.command.events.payloads.OrderStatusUpdatedPayload;
import java.time.LocalDateTime;
import java.util.UUID;

// Event factory class with static methods
/**
 * Utility class providing factory methods to create Order-related events.
 *
 * <p>This class centralizes the construction of domain events for order operations, encapsulating
 * common metadata like a generated event ID, event type, subject ("Order"), timestamp, and the
 * specific payload.
 *
 * <p>Available event types:
 *
 * <ul>
 *   <li>{@code OrderCreated} – when a new order is placed
 *   <li>{@code OrderCancelled} – when an existing order is cancelled
 *   <li>{@code OrderStatusUpdated} – when an order’s status is updated
 * </ul>
 *
 * <p>Each factory method returns an {@code Event<T>} instance, where {@code T} is the payload type
 * carrying the event details.
 *
 * <p><strong>Thread Safety:</strong> All methods are stateless and safe to invoke concurrently.
 */
public class OrderEvents {
  /**
   * Creates a new OrderCreated event.
   *
   * @param payload the payload containing details about the order creation
   * @return an {@code Event<OrderCreatedPayload>} representing the order creation
   */
  public static Event<OrderCreatedPayload> createOrderCreatedEvent(OrderCreatedPayload payload) {
    return new BaseEvent<>(
        UUID.randomUUID().toString(), "OrderCreated", null, "Order", LocalDateTime.now(), payload);
  }

  /**
   * Creates a new OrderCancelled event.
   *
   * @param orderId the unique identifier of the order that was cancelled
   * @param payload the payload containing details about the order cancellation
   * @return an {@code Event<OrderCancelledPayload>} representing the order cancellation
   */
  public static Event<OrderCancelledPayload> createOrderCancelledEvent(
      String orderId, OrderCancelledPayload payload) {
    return new BaseEvent<>(
        UUID.randomUUID().toString(),
        "OrderCancelled",
        orderId,
        "Order",
        LocalDateTime.now(),
        payload);
  }

  /**
   * Creates an event representing an update to an order’s status.
   *
   * @param orderId the unique identifier of the order
   * @param payload the payload containing the updated status information
   * @return an Event<OrderStatusUpdatedPayload> encapsulating this status update
   */
  public static Event<OrderStatusUpdatedPayload> createOrderStatusUpdatedEvent(
      String orderId, OrderStatusUpdatedPayload payload) {
    return new BaseEvent<>(
        UUID.randomUUID().toString(),
        "OrderStatusUpdated",
        orderId,
        "Order",
        LocalDateTime.now(),
        payload);
  }
}
