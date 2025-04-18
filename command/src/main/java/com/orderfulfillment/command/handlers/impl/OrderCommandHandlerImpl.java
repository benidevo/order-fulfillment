package com.orderfulfillment.command.handlers.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfulfillment.command.commands.CancelOrderCommand;
import com.orderfulfillment.command.commands.CreateOrderCommand;
import com.orderfulfillment.command.commands.UpdateOrderStatusCommand;
import com.orderfulfillment.command.domain.EventMessage;
import com.orderfulfillment.command.domain.OrderStatus;
import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.events.impl.OrderEvents;
import com.orderfulfillment.command.events.payloads.OrderCancelledPayload;
import com.orderfulfillment.command.events.payloads.OrderCreatedPayload;
import com.orderfulfillment.command.events.payloads.OrderStatusUpdatedPayload;
import com.orderfulfillment.command.handlers.OrderCommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCommandHandlerImpl implements OrderCommandHandler {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final NewTopic orderEventsTopic;
  private static ObjectMapper objectMapper = new ObjectMapper();

  public OrderCommandHandlerImpl(
      KafkaTemplate<String, Object> kafkaTemplate,
      NewTopic orderEventsTopic,
      ObjectMapper objectMapper) {
    this.orderEventsTopic = orderEventsTopic;
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
    EventMessage message = createEventMessage(event, payload);

    log.info("Publishing event: {}", event);
    kafkaTemplate.send(orderEventsTopic.name(), event.getAggregateId(), message);
  }

  @Override
  public void handle(UpdateOrderStatusCommand command) {
    OrderStatusUpdatedPayload payload =
        new OrderStatusUpdatedPayload(command.orderId(), command.status());
    Event<OrderStatusUpdatedPayload> event =
        OrderEvents.createOrderStatusUpdatedEvent(command.orderId(), payload);
    EventMessage message = createEventMessage(event, payload);

    log.info("Publishing event: {}", message);
    kafkaTemplate.send(orderEventsTopic.name(), event.getAggregateId(), message);
  }

  @Override
  public void handle(CancelOrderCommand command) {
    OrderCancelledPayload payload = new OrderCancelledPayload(command.orderId());
    Event<OrderCancelledPayload> event =
        OrderEvents.createOrderCancelledEvent(command.orderId(), payload);
    EventMessage message = createEventMessage(event, payload);

    log.info("Publishing event: {}", message);
    kafkaTemplate.send(orderEventsTopic.name(), event.getAggregateId(), message);
  }

  /**
   * Creates an EventMessage from an Event and its payload.
   *
   * <p>This method serializes the payload to JSON and constructs an EventMessage containing all the
   * necessary information from the event.
   *
   * @param event The event containing metadata such as eventId, eventType, etc.
   * @param payload The payload object to be serialized and included in the message
   * @return A fully populated EventMessage ready for publishing
   * @throws RuntimeException if the payload cannot be serialized to JSON
   */
  private static EventMessage createEventMessage(Event<?> event, Object payload) {
    String payloadJson;
    try {
      payloadJson = objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      log.error("Error serializing payload for event {}", event.getEventId(), e);
      throw new RuntimeException("Failed to serialize event payload", e);
    }
    EventMessage message =
        new EventMessage(
            event.getEventId(),
            event.getEventType(),
            event.getAggregateId(),
            event.getAggregateType(),
            event.getTimestamp().toString(),
            payloadJson);

    return message;
  }
}
