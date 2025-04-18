package com.orderfulfillment.command.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents an event message in the domain.
 *
 * <p>An EventMessage encapsulates information about an event that occurred within the system,
 * including its identifier, type, related aggregate information, timestamp, and payload. This class
 * is used for event publishing and handling.
 */
@Data
@AllArgsConstructor
public class EventMessage {
  private String eventId;
  private String eventType;
  private String aggregateId;
  private String aggregateType;
  private String timestamp;
  private String payload;
}
