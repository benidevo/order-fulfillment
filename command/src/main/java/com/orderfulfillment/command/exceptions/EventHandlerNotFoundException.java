package com.orderfulfillment.command.exceptions;

/**
 * Exception thrown when no handler is registered for a specific event type. This indicates that the
 * system does not have a defined way to process the given event.
 */
public class EventHandlerNotFoundException extends RuntimeException {
  private final String eventType;

  public EventHandlerNotFoundException(String eventType) {
    super("No handler registered for event: " + eventType);
    this.eventType = eventType;
  }

  public String getEventType() {
    return eventType;
  }
}
