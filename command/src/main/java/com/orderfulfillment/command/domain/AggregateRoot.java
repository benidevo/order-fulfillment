package com.orderfulfillment.command.domain;

import com.orderfulfillment.command.events.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all aggregate roots in the domain. Provides common functionality for event
 * sourcing.
 */
@Getter
@Setter
public abstract class AggregateRoot {
  private String id;
  private long version = 0;
  private final List<Event<?>> uncommittedChanges = new ArrayList<>();
  private final Map<String, Consumer<Event<?>>> eventHandlers = new HashMap<>();

  /**
   * Gets all uncommitted changes (events) that haven't been persisted.
   *
   * @return an unmodifiable list of uncommitted events
   */
  public List<Event<?>> getUncommittedChanges() {
    return Collections.unmodifiableList(uncommittedChanges);
  }

  /**
   * Marks all changes as committed, clearing the uncommitted changes list. Called after events have
   * been successfully persisted to the event store.
   */
  public void markChangesAsCommitted() {
    uncommittedChanges.clear();
  }

  /**
   * Register an event handler for a specific event type.
   *
   * @param eventType The event type string
   * @param handler The handler function that processes the event
   */
  protected void registerHandler(String eventType, Consumer<Event<?>> handler) {
    eventHandlers.put(eventType, handler);
  }

  /**
   * Applies an event to this aggregate and increments its version. The event is not added to
   * uncommitted changes.
   *
   * @param event the event to apply
   * @param isNew whether the event is new (should be added to uncommitted changes)
   */
  protected void applyChange(Event<?> event, boolean isNew) {
    String eventType = event.getEventType();
    Consumer<Event<?>> handler = eventHandlers.get(eventType);

    if (handler == null) {
      throw new RuntimeException("No handler registered for event: " + eventType);
    }

    handler.accept(event);

    if (isNew) {
      uncommittedChanges.add(event);
    } else {
      version = event.getVersion() + 1;
    }
  }

  /**
   * Applies an event to this aggregate and increments its version. The event is also added to
   * uncommitted changes.
   *
   * @param event the event to apply
   */
  protected void applyChange(Event<?> event) {
    applyChange(event, true);
  }

  /**
   * Loads the aggregate state from a list of events. This is typically used when reconstructing the
   * aggregate from the event store.
   *
   * @param events the list of events to load
   */
  public void loadFromHistory(List<Event<?>> events) {
    events.forEach(e -> applyChange(e, false));
    version = events.size();
  }
}
