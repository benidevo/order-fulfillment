package com.orderfulfillment.command.events;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Common interface for all domain events in the system. Includes default methods for common
 * operations.
 *
 * @param <T> The type of the event payload
 */
public interface Event<T> extends Serializable {
  String getEventId();

  String getEventType();

  String getAggregateId();

  String getAggregateType();

  LocalDateTime getTimestamp();

  long getVersion();

  T getPayload();

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
}
