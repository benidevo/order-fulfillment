package com.orderfulfillment.command.exceptions;

/**
 * Exception thrown when an event fails to be published to the event bus. This can occur due to
 * network issues, serialization problems, or other unexpected errors during the publishing process.
 */
public class EventPublishingException extends RuntimeException {
  private final String eventId;

  public EventPublishingException(String eventId, Throwable cause) {
    super("Failed to publish event: " + eventId, cause);
    this.eventId = eventId;
  }

  public String getEventId() {
    return eventId;
  }
}
