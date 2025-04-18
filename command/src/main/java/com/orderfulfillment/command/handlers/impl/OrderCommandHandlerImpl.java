package com.orderfulfillment.command.handlers.impl;

import com.orderfulfillment.command.commands.AllocateInventoryCommand;
import com.orderfulfillment.command.commands.CancelOrderCommand;
import com.orderfulfillment.command.commands.CreateOrderCommand;
import com.orderfulfillment.command.commands.ReturnInventoryCommand;
import com.orderfulfillment.command.commands.UpdateOrderStatusCommand;
import com.orderfulfillment.command.domain.Order;
import com.orderfulfillment.command.domain.OrderItem;
import com.orderfulfillment.command.exceptions.domain.DomainRuleViolationException;
import com.orderfulfillment.command.exceptions.domain.InsufficientInventoryException;
import com.orderfulfillment.command.exceptions.domain.OrderNotFoundException;
import com.orderfulfillment.command.handlers.InventoryCommandHandler;
import com.orderfulfillment.command.handlers.OrderCommandHandler;
import com.orderfulfillment.command.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCommandHandlerImpl implements OrderCommandHandler {
  private final OrderRepository orderRepository;
  private final InventoryCommandHandler inventoryCommandHandler;

  public OrderCommandHandlerImpl(
      OrderRepository orderRepository, InventoryCommandHandler inventoryCommandHandler) {
    this.orderRepository = orderRepository;
    this.inventoryCommandHandler = inventoryCommandHandler;
  }

  @Override
  public void handle(CreateOrderCommand command) {
    log.info("Handling CreateOrderCommand: {}", command);

    Order order =
        Order.createNew(
            command.customerId(),
            command.items(),
            command.shippingAddress(),
            command.billingAddress(),
            command.totalCost());

    orderRepository.save(order);
    log.info("Order created with ID: {}", order.getId());

    for (OrderItem item : command.items()) {
      try {
        inventoryCommandHandler.handle(
            AllocateInventoryCommand.builder()
                .productId(item.getProductId())
                .orderId(order.getId())
                .quantity(item.getQuantity())
                .build());

        log.info(
            "Allocated {} units of product {} to order {}",
            item.getQuantity(),
            item.getProductId(),
            order.getId());
      } catch (InsufficientInventoryException e) {
        log.warn(
            "Insufficient inventory for product {}: requested {}, available {}",
            e.getProductId(),
            e.getRequested(),
            e.getAvailable());
      }
    }
  }

  @Override
  public void handle(UpdateOrderStatusCommand command) {
    log.info("Handling UpdateOrderStatusCommand: {}", command);

    Order order;
    try {
      order = orderRepository.findById(command.orderId());
    } catch (OrderNotFoundException e) {
      log.error("Order not found when updating status: {}", command.orderId());
      throw e;
    }

    try {
      order.updateStatus(command.status());
      orderRepository.save(order);
      log.info("Order status updated: {}", command.orderId());
    } catch (DomainRuleViolationException e) {
      log.error("Domain rule violation when updating order status: {}", e.getMessage());
      throw e;
    }
  }

  @Override
  public void handle(CancelOrderCommand command) {
    log.info("Handling CancelOrderCommand: {}", command);

    Order order;
    try {
      order = orderRepository.findById(command.orderId());
    } catch (OrderNotFoundException e) {
      log.error("Order not found when cancelling: {}", command.orderId());
      throw e;
    }

    try {
      order.cancel();
      orderRepository.save(order);
      log.info("Order cancelled: {}", command.orderId());

      for (OrderItem item : order.getItems()) {
        try {
          inventoryCommandHandler.handle(
              ReturnInventoryCommand.builder()
                  .productId(item.getProductId())
                  .orderId(order.getId())
                  .quantity(item.getQuantity())
                  .build());

          log.info(
              "Returned {} units of product {} from cancelled order {}",
              item.getQuantity(),
              item.getProductId(),
              order.getId());
        } catch (Exception e) {
          log.error(
              "Error returning inventory for product {}: {}", item.getProductId(), e.getMessage());
        }
      }
    } catch (DomainRuleViolationException e) {
      log.error("Domain rule violation when cancelling order: {}", e.getMessage());
      throw e;
    }
  }
}
