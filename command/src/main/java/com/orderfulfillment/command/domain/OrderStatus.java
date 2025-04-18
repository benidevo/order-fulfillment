package com.orderfulfillment.command.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
  CREATED("REGISTERED"),
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
