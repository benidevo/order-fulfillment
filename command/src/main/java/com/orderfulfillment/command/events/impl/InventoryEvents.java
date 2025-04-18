package com.orderfulfillment.command.events.impl;

import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.events.payloads.InventoryAllocatedPayload;
import com.orderfulfillment.command.events.payloads.InventoryReturnedPayload;
import com.orderfulfillment.command.events.payloads.InventoryUpdatedPayload;
import com.orderfulfillment.command.utils.Constants;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Utility class providing factory methods to create Inventory-related events.
 *
 * <p>This class centralizes the construction of domain events for inventory operations,
 * encapsulating common metadata like a generated event ID, event type, subject ("Inventory"),
 * timestamp, and the specific payload.
 *
 * <p>Available event types:
 *
 * <ul>
 *   <li>{@code InventoryUpdated} – when inventory levels are updated
 *   <li>{@code InventoryAllocated} – when inventory is allocated to an order
 *   <li>{@code InventoryReturned} – when inventory is returned from a cancelled order
 * </ul>
 *
 * <p>Each factory method returns an {@code Event<T>} instance, where {@code T} is the payload type
 * carrying the event details.
 *
 * <p><strong>Thread Safety:</strong> All methods are stateless and safe to invoke concurrently.
 */
public class InventoryEvents {

  /**
   * Creates a new InventoryUpdated event.
   *
   * @param inventoryId the unique identifier of the inventory aggregate
   * @param payload the payload containing details about the inventory update
   * @param version the version of the event
   * @return an {@code Event<InventoryUpdatedPayload>} representing the inventory update
   */
  public static Event<InventoryUpdatedPayload> createInventoryUpdatedEvent(
      String inventoryId, InventoryUpdatedPayload payload, long version) {
    return new BaseEvent<>(
        UUID.randomUUID().toString(),
        Constants.INVENTORY_UPDATED_EVENT,
        inventoryId,
        Constants.AGGREGATE_TYPE_INVENTORY,
        LocalDateTime.now(),
        version,
        payload);
  }

  /**
   * Creates a new InventoryAllocated event.
   *
   * @param inventoryId the unique identifier of the inventory aggregate
   * @param payload the payload containing details about the inventory allocation
   * @param version the version of the event
   * @return an {@code Event<InventoryAllocatedPayload>} representing the inventory allocation
   */
  public static Event<InventoryAllocatedPayload> createInventoryAllocatedEvent(
      String inventoryId, InventoryAllocatedPayload payload, long version) {
    return new BaseEvent<>(
        UUID.randomUUID().toString(),
        Constants.INVENTORY_ALLOCATED_EVENT,
        inventoryId,
        Constants.AGGREGATE_TYPE_INVENTORY,
        LocalDateTime.now(),
        version,
        payload);
  }

  /**
   * Creates a new InventoryReturned event.
   *
   * @param inventoryId the unique identifier of the inventory aggregate
   * @param payload the payload containing details about the inventory return
   * @param version the version of the event
   * @return an {@code Event<InventoryReturnedPayload>} representing the inventory return
   */
  public static Event<InventoryReturnedPayload> createInventoryReturnedEvent(
      String inventoryId, InventoryReturnedPayload payload, long version) {
    return new BaseEvent<>(
        UUID.randomUUID().toString(),
        Constants.INVENTORY_RETURNED_EVENT,
        inventoryId,
        Constants.AGGREGATE_TYPE_INVENTORY,
        LocalDateTime.now(),
        version,
        payload);
  }
}
