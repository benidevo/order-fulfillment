package com.orderfulfillment.command.exceptions;

public class EventSerializationException extends RuntimeException {
  private final String eventId;

  public EventSerializationException(String eventId, Throwable cause) {
    super("Failed to serialize/deserialize event: " + eventId, cause);
    this.eventId = eventId;
  }

  public String getEventId() {
    return eventId;
  }
}
