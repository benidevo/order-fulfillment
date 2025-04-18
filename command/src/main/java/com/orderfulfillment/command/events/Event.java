package com.orderfulfillment.command.events;

import com.orderfulfillment.command.events.impl.BaseEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Common interface for all domain events in the system. Includes default methods for common
 * operations.
 *
 * @param <T> The type of the event payload
 */
public interface Event<T> {
  String getEventId();

  String getEventType();

  String getAggregateId();

  String getAggregateType();

  LocalDateTime getTimestamp();

  T getPayload();

  /**
   * Creates a standard set of metadata for an event.
   *
   * @param userId The ID of the user who triggered the event
   * @return A map containing standard metadata entries
   */
  default Map<String, String> createDefaultMetadata(String userId) {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("correlationId", UUID.randomUUID().toString());
    metadata.put("timestamp", LocalDateTime.now().toString());
    metadata.put("userId", userId != null ? userId : "system");
    return metadata;
  }

  /**
   * Provides a human-readable description of the event.
   *
   * @return A string describing the event in a standardized format
   */
  default String getEventDescription() {
    return String.format(
        "%s[%s] for %s:%s v%d at %s",
        getEventType(), getEventId(), getAggregateType(), getAggregateId(), getTimestamp());
  }

  /**
   * Creates a new event with the specified parameters.
   *
   * @param eventType The type of event
   * @param aggregateId The ID of the aggregate this event applies to
   * @param aggregateType The type of aggregate
   * @param version The version of the event
   * @param payload The event payload
   * @param metadata The event metadata
   * @return A new Event instance
   */
  static <T> Event<T> create(
      String eventType,
      String aggregateId,
      String aggregateType,
      int version,
      T payload,
      Map<String, String> metadata) {

    return new BaseEvent<>(
        UUID.randomUUID().toString(),
        eventType,
        aggregateId,
        aggregateType,
        LocalDateTime.now(),
        payload);
  }
}
