package com.orderfulfillment.command.utils;

public class Constants {
  // Kafka topic names
  public static final String ORDER_EVENTS_TOPIC = "order-events";
  public static final String INVENTORY_EVENTS_TOPIC = "inventory-events";

  // Event names
  public static final String ORDER_CREATED_EVENT = "OrderCreated";
  public static final String ORDER_STATUS_UPDATED_EVENT = "OrderStatusUpdated";
  public static final String ORDER_CANCELED_EVENT = "OrderCanceled";
  public static final String ORDER_SHIPPED_EVENT = "OrderShipped";
  public static final String ORDER_DELIVERED_EVENT = "OrderDelivered";

  // Event types
  public static final String EVENT_TYPE_ORDER = "OrderEvent";
  public static final String EVENT_TYPE_INVENTORY = "InventoryEvent";

  // Aggregate types
  public static final String AGGREGATE_TYPE_ORDER = "OrderAggregate";
  public static final String AGGREGATE_TYPE_INVENTORY = "InventoryAggregate";
}
