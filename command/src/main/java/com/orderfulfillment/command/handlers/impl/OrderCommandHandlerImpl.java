package com.orderfulfillment.command.handlers.impl;

import com.orderfulfillment.command.commands.CancelOrderCommand;
import com.orderfulfillment.command.commands.CreateOrderCommand;
import com.orderfulfillment.command.commands.UpdateOrderStatusCommand;
import com.orderfulfillment.command.domain.Order;
import com.orderfulfillment.command.handlers.OrderCommandHandler;
import com.orderfulfillment.command.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCommandHandlerImpl implements OrderCommandHandler {
  private final OrderRepository orderRepository;

  public OrderCommandHandlerImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
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
  }

  @Override
  public void handle(UpdateOrderStatusCommand command) {
    log.info("Handling UpdateOrderStatusCommand: {}", command);

    Order order = orderRepository.findById(command.orderId());
    if (order == null) {
      throw new IllegalArgumentException("Order not found: " + command.orderId());
    }

    order.updateStatus(command.status());
    orderRepository.save(order);
    log.info("Order status updated: {}", command.orderId());
  }

  @Override
  public void handle(CancelOrderCommand command) {
    log.info("Handling CancelOrderCommand: {}", command);

    Order order = orderRepository.findById(command.orderId());
    if (order == null) {
      throw new IllegalArgumentException("Order not found: " + command.orderId());
    }

    order.cancel();
    orderRepository.save(order);
    log.info("Order cancelled: {}", command.orderId());
  }
}
