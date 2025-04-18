package com.orderfulfillment.command.repositories;

import com.orderfulfillment.command.domain.InventoryItem;
import com.orderfulfillment.command.exceptions.ConcurrencyException;
import com.orderfulfillment.command.exceptions.domain.ProductNotFoundException;

/** Repository interface for InventoryItem aggregate operations */
public interface InventoryRepository {

  /**
   * Retrieves an InventoryItem by its product ID.
   *
   * <p>This method reconstructs the InventoryItem's current state by retrieving all associated
   * events from the event store and replaying them in sequence.
   *
   * @param productId the unique identifier of the product
   * @return the reconstructed InventoryItem with its current state
   * @throws ProductNotFoundException if no inventory exists for the given product ID
   */
  InventoryItem findByProductId(String productId);

  /**
   * Retrieves an InventoryItem by its aggregate ID.
   *
   * @param inventoryId the unique identifier of the inventory aggregate
   * @return the reconstructed InventoryItem with its current state
   * @throws ProductNotFoundException if no inventory exists with the given ID
   */
  InventoryItem findById(String inventoryId);

  /**
   * Saves an InventoryItem aggregate to the event store.
   *
   * <p>This method handles optimistic concurrency control by checking the version of the
   * InventoryItem before saving.
   *
   * @param inventoryItem the InventoryItem aggregate to save
   * @throws ConcurrencyException if a concurrency conflict occurs during save
   */
  void save(InventoryItem inventoryItem);

  /**
   * Checks if inventory exists for a specific product.
   *
   * @param productId the unique identifier of the product
   * @return true if inventory exists, false otherwise
   */
  boolean existsByProductId(String productId);
}
