package com.orderfulfillment.command.handlers.impl;

import com.orderfulfillment.command.commands.CancelOrderCommand;
import com.orderfulfillment.command.commands.CreateOrderCommand;
import com.orderfulfillment.command.commands.UpdateOrderStatusCommand;
import com.orderfulfillment.command.domain.OrderStatus;
import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.events.impl.OrderEvents;
import com.orderfulfillment.command.events.payloads.OrderCancelledPayload;
import com.orderfulfillment.command.events.payloads.OrderCreatedPayload;
import com.orderfulfillment.command.events.payloads.OrderStatusUpdatedPayload;
import com.orderfulfillment.command.handlers.OrderCommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCommandHandlerImpl implements OrderCommandHandler {
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public OrderCommandHandlerImpl(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void handle(CreateOrderCommand command) {
    OrderCreatedPayload payload =
        OrderCreatedPayload.builder()
            .customerId(command.customerId())
            .quantity(command.items().size())
            .items(command.items())
            .status(OrderStatus.REGISTERED)
            .shippingAddress(command.shippingAddress())
            .billingAddress(command.billingAddress())
            .totalCost(command.totalCost())
            .build();

    Event<OrderCreatedPayload> event = OrderEvents.createOrderCreatedEvent(payload);

    log.info("Publishing event: {}", event);
  }

  @Override
  public void handle(UpdateOrderStatusCommand command) {
    OrderStatusUpdatedPayload payload =
        new OrderStatusUpdatedPayload(command.orderId(), command.status());
    Event<OrderStatusUpdatedPayload> event =
        OrderEvents.createOrderStatusUpdatedEvent(command.orderId(), payload);

    log.info("Publishing event: {}", event);
  }

  @Override
  public void handle(CancelOrderCommand command) {
    OrderCancelledPayload payload = new OrderCancelledPayload(command.orderId());
    Event<OrderCancelledPayload> event =
        OrderEvents.createOrderCancelledEvent(command.orderId(), payload);

    log.info("Publishing event: {}", event);
  }
}
