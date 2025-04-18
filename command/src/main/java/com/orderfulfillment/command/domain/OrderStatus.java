package com.orderfulfillment.command.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the status of an order in the order fulfillment system.
 *
 * <p>This enum defines various states an order can be in, such as registered, cancelled, shipped,
 * partially shipped, delivered, and partially delivered.
 *
 * <p>Each status is represented by a string value that can be serialized and deserialized using
 * Jackson annotations.
 */
public enum OrderStatus {
  REGISTERED("REGISTERED"),
  CANCELLED("CANCELLED"),
  SHIPPED("SHIPPED"),
  PARTIALLY_SHIPPED("PARTIALLY_SHIPPED"),
  DELIVERED("DELIVERED"),
  PARTIALLY_DELIVERED("PARTIALLY_DELIVERED");

  private String status;

  private OrderStatus(String status) {
    this.status = status;
  }

  @JsonValue
  public String getStatus() {
    return status;
  }

  @JsonCreator
  public static OrderStatus from(String value) {
    for (OrderStatus s : values()) {
      if (s.name().equals(value)) {
        return s;
      }
    }
    throw new IllegalArgumentException("Unknown OrderStatus: " + value);
  }
}
