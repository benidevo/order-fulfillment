package com.orderfulfillment.command.domain;

import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.events.impl.OrderEvents;
import com.orderfulfillment.command.events.payloads.OrderCancelledPayload;
import com.orderfulfillment.command.events.payloads.OrderCreatedPayload;
import com.orderfulfillment.command.events.payloads.OrderStatusUpdatedPayload;
import com.orderfulfillment.command.exceptions.domain.CancelledOrderModificationException;
import com.orderfulfillment.command.exceptions.domain.OrderCannotBeCancelledException;
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

    Event<OrderCreatedPayload> event = OrderEvents.createOrderCreatedEvent(orderId, payload, 0);
    order.applyChange(event);
    return order;
  }

  /**
   * Updates the status of the order.
   *
   * <p>This method ensures that an order that has been cancelled cannot be modified.
   *
   * @param newStatus the new status to set
   * @throws CancelledOrderModificationException if the order has been cancelled
   */
  public void updateStatus(OrderStatus newStatus) {
    if (this.status == OrderStatus.CANCELLED) {
      throw new CancelledOrderModificationException(getId());
    }

    if (!isValidStatusTransition(newStatus)) {
      throw new IllegalStateException(
          String.format("Invalid status transition from %s to %s", this.status, newStatus));
    }

    OrderStatusUpdatedPayload payload = new OrderStatusUpdatedPayload(getId(), newStatus);
    Event<OrderStatusUpdatedPayload> event =
        OrderEvents.createOrderStatusUpdatedEvent(getId(), payload, getVersion());

    applyChange(event);
  }

  /**
   * Cancels the order.
   *
   * <p>This method ensures that an order that has been shipped or delivered cannot be cancelled
   *
   * @throws OrderCannotBeCancelledException if the order cannot be cancelled
   */
  public void cancel() {
    if (status == OrderStatus.SHIPPED
        || status == OrderStatus.DELIVERED
        || status == OrderStatus.PARTIALLY_SHIPPED
        || status == OrderStatus.PARTIALLY_DELIVERED) {
      throw new OrderCannotBeCancelledException(getId(), status.name());
    }

    OrderCancelledPayload payload = new OrderCancelledPayload(getId());
    Event<OrderCancelledPayload> event =
        OrderEvents.createOrderCancelledEvent(getId(), payload, getVersion());

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
