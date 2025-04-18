package com.orderfulfillment.command.domain;

import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.events.impl.InventoryEvents;
import com.orderfulfillment.command.events.payloads.InventoryAllocatedPayload;
import com.orderfulfillment.command.events.payloads.InventoryReturnedPayload;
import com.orderfulfillment.command.events.payloads.InventoryUpdatedPayload;
import com.orderfulfillment.command.exceptions.domain.InsufficientInventoryException;
import com.orderfulfillment.command.utils.Constants;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents inventory for a specific product in the system.
 *
 * <p>This class is responsible for managing inventory levels, including tracking available
 * quantity, allocating inventory to orders, and restoring inventory when orders are cancelled.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class InventoryItem extends AggregateRoot {
  private String productId;
  private int availableQuantity;
  private int allocatedQuantity;

  @SuppressWarnings("unchecked")
  public InventoryItem() {
    registerHandler(
        Constants.INVENTORY_UPDATED_EVENT,
        event -> applyInventoryUpdated((Event<InventoryUpdatedPayload>) event));
    registerHandler(
        Constants.INVENTORY_ALLOCATED_EVENT,
        event -> applyInventoryAllocated((Event<InventoryAllocatedPayload>) event));
    registerHandler(
        Constants.INVENTORY_RETURNED_EVENT,
        event -> applyInventoryReturned((Event<InventoryReturnedPayload>) event));
  }

  /**
   * Creates a new InventoryItem for a product.
   *
   * @param productId the product identifier
   * @param quantity the initial available quantity
   * @return a new InventoryItem instance
   */
  public static InventoryItem createNew(String productId, int quantity) {
    InventoryItem inventoryItem = new InventoryItem();
    String inventoryId = UUID.randomUUID().toString();

    InventoryUpdatedPayload payload = new InventoryUpdatedPayload(productId, quantity);
    Event<InventoryUpdatedPayload> event =
        InventoryEvents.createInventoryUpdatedEvent(inventoryId, payload, 0);

    inventoryItem.applyChange(event);
    return inventoryItem;
  }

  /**
   * Updates the available quantity of this inventory item.
   *
   * @param newQuantity the new available quantity
   */
  public void updateQuantity(int newQuantity) {
    if (newQuantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative");
    }

    if (newQuantity < availableQuantity
        && (newQuantity + allocatedQuantity) < allocatedQuantity + availableQuantity) {
      throw new IllegalArgumentException("Cannot reduce quantity below allocated amount");
    }

    InventoryUpdatedPayload payload = new InventoryUpdatedPayload(productId, newQuantity);
    Event<InventoryUpdatedPayload> event =
        InventoryEvents.createInventoryUpdatedEvent(getId(), payload, getVersion());

    applyChange(event);
  }

  /**
   * Allocates a specific quantity of this inventory item to an order.
   *
   * @param orderId the order identifier to allocate to
   * @param quantity the quantity to allocate
   * @throws InsufficientInventoryException if there is not enough available inventory
   */
  public void allocate(String orderId, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Allocation quantity must be positive");
    }

    if (quantity > availableQuantity) {
      throw new InsufficientInventoryException(productId, quantity, availableQuantity);
    }

    InventoryAllocatedPayload payload = new InventoryAllocatedPayload(productId, orderId, quantity);
    Event<InventoryAllocatedPayload> event =
        InventoryEvents.createInventoryAllocatedEvent(getId(), payload, getVersion());

    applyChange(event);
  }

  /**
   * Returns previously allocated inventory when an order is cancelled.
   *
   * @param orderId the order identifier
   * @param quantity the quantity to return
   */
  public void returnInventory(String orderId, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Return quantity must be positive");
    }

    if (quantity > allocatedQuantity) {
      throw new IllegalArgumentException("Cannot return more than allocated quantity");
    }

    InventoryReturnedPayload payload = new InventoryReturnedPayload(productId, orderId, quantity);
    Event<InventoryReturnedPayload> event =
        InventoryEvents.createInventoryReturnedEvent(getId(), payload, getVersion());

    applyChange(event);
  }

  /**
   * Checks if there is sufficient inventory available for allocation.
   *
   * @param quantity the quantity to check
   * @return true if sufficient inventory is available, false otherwise
   */
  public boolean hasSufficientInventory(int quantity) {
    return quantity <= availableQuantity;
  }

  /**
   * Gets the total quantity (available + allocated).
   *
   * @return the total quantity
   */
  public int getTotalQuantity() {
    return availableQuantity + allocatedQuantity;
  }

  private void applyInventoryUpdated(Event<InventoryUpdatedPayload> event) {
    InventoryUpdatedPayload payload = event.getPayload();
    setId(event.getAggregateId());
    productId = payload.productId();
    availableQuantity = payload.quantity();
  }

  private void applyInventoryAllocated(Event<InventoryAllocatedPayload> event) {
    InventoryAllocatedPayload payload = event.getPayload();
    availableQuantity -= payload.quantity();
    allocatedQuantity += payload.quantity();
  }

  private void applyInventoryReturned(Event<InventoryReturnedPayload> event) {
    InventoryReturnedPayload payload = event.getPayload();
    availableQuantity += payload.quantity();
    allocatedQuantity -= payload.quantity();
  }
}
