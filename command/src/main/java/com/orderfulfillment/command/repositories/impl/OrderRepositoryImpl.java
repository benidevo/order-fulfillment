package com.orderfulfillment.command.repositories.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfulfillment.command.domain.EventMessage;
import com.orderfulfillment.command.domain.Order;
import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.exceptions.ConcurrencyException;
import com.orderfulfillment.command.exceptions.EventPublishingException;
import com.orderfulfillment.command.exceptions.EventSerializationException;
import com.orderfulfillment.command.exceptions.domain.OrderNotFoundException;
import com.orderfulfillment.command.repositories.OrderRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the OrderRepository interface for storing and retrieving Order aggregates using
 * an event-sourcing pattern.
 *
 * <p>This repository maintains an in-memory event store while also publishing events to Kafka to
 * ensure events are durably stored. It handles optimistic concurrency control by checking version
 * numbers before saving changes to prevent conflicts when multiple processes attempt to modify the
 * same order simultaneously.
 *
 * @see OrderRepository
 * @see Order
 * @see Event
 */
@Slf4j
@Repository
public class OrderRepositoryImpl implements OrderRepository {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final NewTopic topic;
  private final ObjectMapper objectMapper;
  private final Map<String, List<Event<?>>> eventStore = new HashMap<>();

  public OrderRepositoryImpl(
      KafkaTemplate<String, Object> kafkaTemplate, NewTopic topic, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
    this.objectMapper = objectMapper;
  }

  public Order findById(String orderId) {
    List<Event<?>> events = eventStore.getOrDefault(orderId, new ArrayList<>());
    if (events.isEmpty()) {
      throw new OrderNotFoundException(orderId);
    }

    Order order = new Order();
    order.loadFromHistory(events);
    return order;
  }

  public void save(Order order) {
    List<Event<?>> currentEvents = eventStore.getOrDefault(order.getId(), new ArrayList<>());
    long expectedVersion = currentEvents.size();

    if (order.getVersion() != expectedVersion && !currentEvents.isEmpty()) {
      log.error(
          "Concurrency conflict for order {}: expected version {}, but found {}",
          order.getId(),
          expectedVersion,
          order.getVersion());
      throw new ConcurrencyException(order.getId(), expectedVersion, order.getVersion());
    }

    List<Event<?>> uncommittedEvents = order.getUncommittedChanges();
    List<CompletableFuture<?>> futures = new ArrayList<>();

    for (Event<?> event : uncommittedEvents) {
      try {
        EventMessage message = createEventMessage(event, event.getPayload());

        log.info("Publishing event {} to topic {}", event.getEventId(), topic.name());
        futures.add(
            kafkaTemplate
                .send(topic.name(), event.getAggregateId(), message)
                .toCompletableFuture());

        if (!eventStore.containsKey(order.getId())) {
          eventStore.put(order.getId(), new ArrayList<>());
        }
        eventStore.get(order.getId()).add(event);
      } catch (Exception e) {
        throw new EventPublishingException(event.getEventId(), e);
      }
    }

    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    } catch (Exception e) {
      throw new EventPublishingException("Multiple events", e);
    }

    log.info("Order {} saved with {} events", order.getId(), uncommittedEvents.size());

    order.setVersion(currentEvents.size() + uncommittedEvents.size());
    order.markChangesAsCommitted();
  }

  /**
   * Creates an event message for publishing to the event store.
   *
   * <p>This method converts an event object to a serialized EventMessage by serializing the event
   * payload to JSON format.
   *
   * @param event The domain event containing metadata such as ID, type, and aggregate information
   * @param payload The payload object to be serialized into JSON
   * @return An EventMessage containing all event metadata and the serialized payload
   * @throws EventSerializationException if serialization fails
   */
  private EventMessage createEventMessage(Event<?> event, Object payload) {
    String payloadJson;
    try {
      payloadJson = objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      log.error("Error serializing payload for event {}", event.getEventId(), e);
      throw new EventSerializationException(event.getEventId(), e);
    }

    return new EventMessage(
        event.getEventId(),
        event.getEventType(),
        event.getAggregateId(),
        event.getAggregateType(),
        event.getTimestamp().toString(),
        payloadJson);
  }
}
