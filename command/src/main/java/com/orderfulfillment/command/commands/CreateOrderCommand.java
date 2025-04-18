package com.orderfulfillment.command.commands;

import com.orderfulfillment.command.domain.Address;
import com.orderfulfillment.command.domain.Money;
import com.orderfulfillment.command.domain.OrderItem;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

/**
 * Immutable command record for creating a new order in the fulfillment system. Encapsulates all
 * necessary details required to process and fulfill the order.
 *
 * @param customerId the unique identifier of the customer placing the order
 * @param items the list of items included in the order
 * @param shippingAddress the address where the order will be shipped
 * @param billingAddress the address used for billing; may differ from shipping address
 * @param totalCost the total monetary amount for the order, including taxes and fees
 * @param issuedAt the timestamp when the customer placed the order
 */
@Builder
public record CreateOrderCommand(
    String customerId,
    List<OrderItem> items,
    Address shippingAddress,
    Address billingAddress,
    Money totalCost,
    LocalDateTime issuedAt) {}
