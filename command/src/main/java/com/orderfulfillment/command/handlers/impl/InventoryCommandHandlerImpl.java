package com.orderfulfillment.command.handlers.impl;

import com.orderfulfillment.command.commands.AllocateInventoryCommand;
import com.orderfulfillment.command.commands.ReturnInventoryCommand;
import com.orderfulfillment.command.commands.UpdateInventoryCommand;
import com.orderfulfillment.command.domain.InventoryItem;
import com.orderfulfillment.command.exceptions.domain.InsufficientInventoryException;
import com.orderfulfillment.command.exceptions.domain.ProductNotFoundException;
import com.orderfulfillment.command.handlers.InventoryCommandHandler;
import com.orderfulfillment.command.repositories.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryCommandHandlerImpl implements InventoryCommandHandler {

  private final InventoryRepository inventoryRepository;

  public InventoryCommandHandlerImpl(InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
  }

  @Override
  public void handle(UpdateInventoryCommand command) {
    log.info("Handling UpdateInventoryCommand: {}", command);

    InventoryItem inventoryItem;

    try {
      inventoryItem = inventoryRepository.findByProductId(command.productId());
      inventoryItem.updateQuantity(command.quantity());
    } catch (ProductNotFoundException e) {
      log.info("No inventory found for product {}, creating new inventory", command.productId());
      inventoryItem = InventoryItem.createNew(command.productId(), command.quantity());
    }

    inventoryRepository.save(inventoryItem);
    log.info("Inventory updated for product {}: {}", command.productId(), command.quantity());
  }

  @Override
  public void handle(AllocateInventoryCommand command) {
    log.info("Handling AllocateInventoryCommand: {}", command);

    InventoryItem inventoryItem;
    try {
      inventoryItem = inventoryRepository.findByProductId(command.productId());
    } catch (ProductNotFoundException e) {
      log.error("Product not found when allocating inventory: {}", command.productId());
      throw e;
    }

    try {
      inventoryItem.allocate(command.orderId(), command.quantity());
      inventoryRepository.save(inventoryItem);
      log.info(
          "Allocated {} units of product {} to order {}",
          command.quantity(),
          command.productId(),
          command.orderId());
    } catch (InsufficientInventoryException e) {
      log.error(
          "Insufficient inventory for product {}: requested {}, available {}",
          e.getProductId(),
          e.getRequested(),
          e.getAvailable());
      throw e;
    }
  }

  @Override
  public void handle(ReturnInventoryCommand command) {
    log.info("Handling ReturnInventoryCommand: {}", command);

    InventoryItem inventoryItem;
    try {
      inventoryItem = inventoryRepository.findByProductId(command.productId());
    } catch (ProductNotFoundException e) {
      log.error("Product not found when returning inventory: {}", command.productId());
      throw e;
    }

    try {
      inventoryItem.returnInventory(command.orderId(), command.quantity());
      inventoryRepository.save(inventoryItem);
      log.info(
          "Returned {} units of product {} from order {}",
          command.quantity(),
          command.productId(),
          command.orderId());
    } catch (IllegalArgumentException e) {
      log.error("Error returning inventory: {}", e.getMessage());
      throw e;
    }
  }
}
