package com.orderfulfillment.command.domain;

import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.events.impl.OrderEvents;
import com.orderfulfillment.command.events.payloads.OrderCancelledPayload;
import com.orderfulfillment.command.events.payloads.OrderCreatedPayload;
import com.orderfulfillment.command.events.payloads.OrderStatusUpdatedPayload;
import com.orderfulfillment.command.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents an order in the system. This class is responsible for managing the state and behavior
 * of an order, including its items, status, and associated addresses.
 *
 * <p>It uses event sourcing to manage its state changes, applying events to update its internal
 * state.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Order extends AggregateRoot {
  private String customerId;
  private List<OrderItem> items = new ArrayList<>();
  private OrderStatus status;
  private Address shippingAddress;
  private Address billingAddress;
  private Money totalCost;

  @SuppressWarnings("unchecked")
  public Order() {
    registerHandler(
        Constants.ORDER_CREATED_EVENT,
        event -> applyOrderCreated((Event<OrderCreatedPayload>) event));
    registerHandler(
        Constants.ORDER_STATUS_UPDATED_EVENT,
        event -> applyStatusUpdate((Event<OrderStatusUpdatedPayload>) event));
    registerHandler(
        Constants.ORDER_CANCELED_EVENT,
        event -> applyOrderCancelled((Event<OrderCancelledPayload>) event));
  }

  /**
   * Creates a new Order from a command.
   *
   * @param customerId the customer ID
   * @param items the order items
   * @param shippingAddress the shipping address
   * @param billingAddress the billing address
   * @param totalCost the total cost
   * @return a new Order instance
   */
  public static Order createNew(
      String customerId,
      List<OrderItem> items,
      Address shippingAddress,
      Address billingAddress,
      Money totalCost) {

    Order order = new Order();
    String orderId = UUID.randomUUID().toString();

    OrderCreatedPayload payload =
        OrderCreatedPayload.builder()
            .customerId(customerId)
            .quantity(items.size())
            .items(items)
            .status(OrderStatus.REGISTERED)
            .shippingAddress(shippingAddress)
            .billingAddress(billingAddress)
            .totalCost(totalCost)
            .build();

    Event<OrderCreatedPayload> event = OrderEvents.createOrderCreatedEvent(orderId, payload, 1);
    order.applyChange(event);
    return order;
  }

  /**
   * Updates the order status to the specified new status.
   *
   * <p>This method enforces the following business rules:
   *
   * <ul>
   *   <li>A cancelled order cannot be updated
   *   <li>The status transition must be valid according to the allowed order status workflow
   * </ul>
   *
   * <p>When the status is updated, an OrderStatusUpdatedEvent is generated and applied to the
   * order.
   *
   * @param newStatus the new status to set for this order
   * @throws IllegalStateException if the order is already cancelled or if the status transition is
   *     invalid
   */
  public void updateStatus(OrderStatus newStatus) {
    if (this.status == OrderStatus.CANCELLED) {
      throw new IllegalStateException("Cannot update a cancelled order");
    }

    if (!isValidStatusTransition(newStatus)) {
      throw new IllegalStateException(
          String.format("Invalid status transition from %s to %s", this.status, newStatus));
    }

    OrderStatusUpdatedPayload payload = new OrderStatusUpdatedPayload(getId(), newStatus);
    Event<OrderStatusUpdatedPayload> event =
        OrderEvents.createOrderStatusUpdatedEvent(getId(), payload, getVersion() + 1);

    applyChange(event);
  }

  /**
   * Cancels the order.
   *
   * <p>This method enforces the following business rules:
   *
   * <ul>
   *   <li>An order that has been shipped or delivered cannot be cancelled
   * </ul>
   *
   * <p>When the order is cancelled, an OrderCancelledEvent is generated and applied to the order.
   *
   * @throws IllegalStateException if the order has already been shipped or delivered
   */
  public void cancel() {
    if (status == OrderStatus.SHIPPED
        || status == OrderStatus.DELIVERED
        || status == OrderStatus.PARTIALLY_SHIPPED
        || status == OrderStatus.PARTIALLY_DELIVERED) {
      throw new IllegalStateException("Cannot cancel an order that has been shipped or delivered");
    }

    OrderCancelledPayload payload = new OrderCancelledPayload(getId());
    Event<OrderCancelledPayload> event =
        OrderEvents.createOrderCancelledEvent(getId(), payload, getVersion() + 1);

    applyChange(event);
  }

  /**
   * Handles the OrderCreated event.
   *
   * @param event the event to apply
   */
  private void applyOrderCreated(Event<OrderCreatedPayload> event) {
    OrderCreatedPayload payload = event.getPayload();
    setId(event.getAggregateId());
    this.customerId = payload.customerId();
    this.items = payload.items();
    this.status = payload.status();
    this.shippingAddress = payload.shippingAddress();
    this.billingAddress = payload.billingAddress();
    this.totalCost = payload.totalCost();
  }

  /**
   * Handles the OrderStatusUpdated event.
   *
   * @param event the event to apply
   */
  private void applyStatusUpdate(Event<OrderStatusUpdatedPayload> event) {
    OrderStatusUpdatedPayload payload = event.getPayload();
    this.status = payload.status();
  }

  /**
   * Handles the OrderCancelled event.
   *
   * @param event the event to apply
   */
  private void applyOrderCancelled(Event<OrderCancelledPayload> event) {
    this.status = OrderStatus.CANCELLED;
  }

  /**
   * Validates if a status transition is allowed based on business rules.
   *
   * @param newStatus the target status
   * @return true if the transition is valid, false otherwise
   */
  private boolean isValidStatusTransition(OrderStatus newStatus) {
    switch (this.status) {
      case REGISTERED:
        return newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.PARTIALLY_SHIPPED;

      case PARTIALLY_SHIPPED:
        return newStatus == OrderStatus.SHIPPED
            || newStatus == OrderStatus.PARTIALLY_DELIVERED
            || newStatus == OrderStatus.DELIVERED;

      case SHIPPED:
        return newStatus == OrderStatus.DELIVERED;

      case PARTIALLY_DELIVERED:
        return newStatus == OrderStatus.DELIVERED;

      default:
        return false;
    }
  }
}
