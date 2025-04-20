package com.orderfulfillment.command.repositories.impl;

import com.orderfulfillment.command.domain.EventMessage;
import com.orderfulfillment.command.domain.InventoryItem;
import com.orderfulfillment.command.events.Event;
import com.orderfulfillment.command.exceptions.ConcurrencyException;
import com.orderfulfillment.command.exceptions.EventPublishingException;
import com.orderfulfillment.command.exceptions.domain.ProductNotFoundException;
import com.orderfulfillment.command.repositories.InventoryRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the InventoryRepository interface for storing and retrieving InventoryItem
 * aggregates using an event-sourcing pattern.
 *
 * <p>This repository maintains an in-memory event store while also publishing events to Kafka to
 * ensure events are durably stored. It handles optimistic concurrency control by checking version
 * numbers before saving changes to prevent conflicts when multiple processes attempt to modify the
 * same inventory simultaneously.
 */
@Slf4j
@Repository
public class InventoryRepositoryImpl implements InventoryRepository {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final NewTopic topic;
  private final Map<String, List<Event<?>>> eventStore = new HashMap<>();
  private final Map<String, String> productToInventoryMap = new HashMap<>();

  public InventoryRepositoryImpl(
      KafkaTemplate<String, Object> kafkaTemplate,
      @Qualifier("inventoryEventsTopic") NewTopic inventoryTopic) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = inventoryTopic;
  }

  @Override
  public InventoryItem findByProductId(String productId) {
    String inventoryId = productToInventoryMap.get(productId);
    if (inventoryId == null) {
      throw new ProductNotFoundException(productId);
    }

    return findById(inventoryId);
  }

  @Override
  public InventoryItem findById(String inventoryId) {
    List<Event<?>> events = eventStore.getOrDefault(inventoryId, new ArrayList<>());
    if (events.isEmpty()) {
      throw new ProductNotFoundException("No inventory found with ID: " + inventoryId);
    }

    InventoryItem inventoryItem = new InventoryItem();
    inventoryItem.loadFromHistory(events);
    productToInventoryMap.put(inventoryItem.getProductId(), inventoryId);

    return inventoryItem;
  }

  @Override
  public void save(InventoryItem inventoryItem) {
    List<Event<?>> currentEvents =
        eventStore.getOrDefault(inventoryItem.getId(), new ArrayList<>());
    long expectedVersion = currentEvents.size();

    if (inventoryItem.getVersion() != expectedVersion && !currentEvents.isEmpty()) {
      log.error(
          "Concurrency conflict for inventory {}: expected version {}, but found {}",
          inventoryItem.getId(),
          expectedVersion,
          inventoryItem.getVersion());
      throw new ConcurrencyException(
          inventoryItem.getId(), expectedVersion, inventoryItem.getVersion());
    }

    List<Event<?>> uncommittedEvents = inventoryItem.getUncommittedChanges();
    List<CompletableFuture<?>> futures = new ArrayList<>();

    for (Event<?> event : uncommittedEvents) {
      try {
        EventMessage message = createEventMessage(event, event.getPayload());

        log.info("Publishing inventory event {} to topic {}", event.getEventId(), topic.name());
        futures.add(
            kafkaTemplate
                .send(topic.name(), event.getAggregateId(), message)
                .toCompletableFuture());

        if (!eventStore.containsKey(inventoryItem.getId())) {
          eventStore.put(inventoryItem.getId(), new ArrayList<>());
        }
        eventStore.get(inventoryItem.getId()).add(event);

        productToInventoryMap.put(inventoryItem.getProductId(), inventoryItem.getId());

      } catch (Exception e) {
        throw new EventPublishingException(event.getEventId(), e);
      }
    }

    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    } catch (Exception e) {
      throw new EventPublishingException("Multiple inventory events", e);
    }

    log.info("Inventory {} saved with {} events", inventoryItem.getId(), uncommittedEvents.size());

    inventoryItem.setVersion(currentEvents.size() + uncommittedEvents.size());
    inventoryItem.markChangesAsCommitted();
  }

  @Override
  public boolean existsByProductId(String productId) {
    return productToInventoryMap.containsKey(productId);
  }

  private EventMessage createEventMessage(Event<?> event, Object payload) {
    return new EventMessage(
        event.getEventId(),
        event.getEventType(),
        event.getAggregateId(),
        event.getAggregateType(),
        event.getTimestamp().toString(),
        payload);
  }
}
