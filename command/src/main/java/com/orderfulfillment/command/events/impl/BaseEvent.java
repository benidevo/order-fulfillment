package com.orderfulfillment.command.events.impl;

import com.orderfulfillment.command.events.Event;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Base implementation of the {@link Event} interface representing a domain event.
 *
 * <p>This class encapsulates the core metadata and payload for any event:
 *
 * <ul>
 *   <li><b>eventId</b>: a unique identifier for this event
 *   <li><b>eventType</b>: a string denoting the type or category of the event
 *   <li><b>aggregateId</b>: the identifier of the aggregate that produced the event
 *   <li><b>aggregateType</b>: the type of the aggregate that produced the event
 *   <li><b>timestamp</b>: the date and time when the event was created
 *   <li><b>payload</b>: the event-specific data of type {@code T}
 * </ul>
 *
 * @param <T> the type of the event payload
 */
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class BaseEvent<T> implements Event<T> {

  private final String eventId;
  private final String eventType;
  private final String aggregateId;
  private final String aggregateType;
  private final LocalDateTime timestamp;
  private final T payload;
}
