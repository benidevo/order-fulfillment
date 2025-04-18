package com.orderfulfillment.command.repositories;

import com.orderfulfillment.command.domain.Order;
import com.orderfulfillment.command.exceptions.ConcurrencyException;

/** Repository interface for Order aggregate operations using event sourcing. */
public interface OrderRepository {
  /**
   * Finds an Order by its unique identifier.
   *
   * <p>Retrieves all events for the specified order ID from the event store and reconstructs the
   * Order's current state by replaying these events.
   *
   * @param orderId the unique identifier of the order to retrieve
   * @return the reconstructed Order or null if no events exist for the given ID
   */
  Order findById(String orderId);

  /**
   * Saves an Order by persisting its uncommitted events.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Validates the Order's version to prevent concurrency conflicts
   *   <li>Persists all uncommitted events to the event store
   *   <li>Publishes events to the event bus (e.g., Kafka)
   *   <li>Marks the events as committed in the Order aggregate
   * </ul>
   *
   * @param order the Order aggregate with uncommitted events to save
   * @throws ConcurrencyException if the Order's version doesn't match the expected version in the
   *     event store
   */
  void save(Order order);
}
