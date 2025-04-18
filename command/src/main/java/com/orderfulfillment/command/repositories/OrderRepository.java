package com.orderfulfillment.command.repositories;

import com.orderfulfillment.command.domain.Order;
import com.orderfulfillment.command.exceptions.ConcurrencyException;
import com.orderfulfillment.command.exceptions.domain.OrderNotFoundException;

/** Repository interface for Order aggregate operations using event sourcing. */
public interface OrderRepository {
  /**
   * Retrieves an Order by its unique identifier.
   *
   * <p>This method reconstructs the Order's current state by retrieving all associated events from
   * the event store and replaying them in sequence.
   *
   * @param orderId the unique identifier of the order to retrieve
   * @return the reconstructed Order with its current state
   * @throws OrderNotFoundException if no order exists with the given ID
   */
  Order findById(String orderId);

  /**
   * Saves an Order aggregate to the event store.
   *
   * <p>This method handles optimistic concurrency control by checking the version of the Order
   * before saving.
   *
   * @param order the Order aggregate to save
   * @throws ConcurrencyException if a concurrency conflict occurs during save
   */
  void save(Order order);
}
