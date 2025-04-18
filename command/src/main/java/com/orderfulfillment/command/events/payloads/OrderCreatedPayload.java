package com.orderfulfillment.command.events.payloads;

import com.orderfulfillment.command.domain.Address;
import com.orderfulfillment.command.domain.Money;
import com.orderfulfillment.command.domain.OrderItem;
import com.orderfulfillment.command.domain.OrderStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderCreatedPayload(
    String customerId,
    int quantity,
    List<OrderItem> items,
    OrderStatus status,
    Address shippingAddress,
    Address billingAddress,
    Money totalCost) {}
