package com.orderfulfillment.command.api;

import com.orderfulfillment.command.api.dtos.OrderCreateDto;
import com.orderfulfillment.command.api.dtos.OrderCreateDto.AddressDto;
import com.orderfulfillment.command.api.dtos.OrderStatusUpdateDto;
import com.orderfulfillment.command.api.dtos.ResponseDto;
import com.orderfulfillment.command.commands.CancelOrderCommand;
import com.orderfulfillment.command.commands.CreateOrderCommand;
import com.orderfulfillment.command.commands.UpdateOrderStatusCommand;
import com.orderfulfillment.command.domain.Address;
import com.orderfulfillment.command.domain.Money;
import com.orderfulfillment.command.domain.OrderItem;
import com.orderfulfillment.command.handlers.OrderCommandHandler;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/orders")
public class OrdersApi {
  private final OrderCommandHandler orderCommandHandler;

  public OrdersApi(OrderCommandHandler orderCommandHandler) {
    this.orderCommandHandler = orderCommandHandler;
  }

  /**
   * Handles HTTP POST requests to register a new order.
   *
   * <p>Validates the incoming {@code OrderCreateDto}, converts its items to domain {@link
   * OrderItem} objects, builds a {@link CreateOrderCommand} with customer and address information,
   * logs the command, and returns a success response.
   *
   * @param orderDto the DTO containing order data (customerId, items, shippingAddress,
   *     billingAddress, totalCost, issuedAt); must be valid
   * @return a {@code ResponseEntity<ResponseDto>} with success set to true
   * @throws MethodArgumentNotValidException if {@code orderDto} fails validation
   */
  @PostMapping
  public ResponseEntity<ResponseDto> registerOrder(@Valid @RequestBody OrderCreateDto orderDto) {
    List<OrderItem> items =
        orderDto.items().stream()
            .map(
                item ->
                    new OrderItem(item.productId(), item.quantity(), toDomainMoney(item.price())))
            .collect(Collectors.toList());

    CreateOrderCommand command =
        CreateOrderCommand.builder()
            .customerId(orderDto.customerId())
            .items(items)
            .shippingAddress(toDomainAddress(orderDto.shippingAddress()))
            .billingAddress(toDomainAddress(orderDto.billingAddress()))
            .totalCost(toDomainMoney(orderDto.totalCost()))
            .issuedAt(orderDto.issuedAt())
            .build();

    log.info("Registering new order");

    orderCommandHandler.handle(command);

    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }

  /**
   * Cancels the order identified by the given orderId.
   *
   * @param orderId the unique identifier of the order to cancel
   * @return a ResponseEntity containing a ResponseDto with the cancellation result
   */
  @DeleteMapping(value = "/{orderId}")
  public ResponseEntity<ResponseDto> cancelOrder(@PathVariable String orderId) {
    CancelOrderCommand command = new CancelOrderCommand(orderId);
    log.info("Cancelling order: {}", orderId);

    orderCommandHandler.handle(command);

    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }

  /**
   * Updates the status of an existing order.
   *
   * @param orderId the unique identifier of the order to update
   * @param orderStatus the DTO containing the new status value for the order
   * @return a ResponseEntity containing a ResponseDto indicating whether the status update was
   *     successful
   */
  @PutMapping(value = "/{orderId}/status")
  public ResponseEntity<ResponseDto> updateOrderStatus(
      @PathVariable String orderId, @Valid @RequestBody OrderStatusUpdateDto orderStatus) {
    UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderId, orderStatus.status());
    log.info("Updating order status for order: {}", orderId);

    orderCommandHandler.handle(command);

    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }

  private Address toDomainAddress(AddressDto addressDto) {
    return Address.builder()
        .street(addressDto.street())
        .city(addressDto.city())
        .state(addressDto.state())
        .zipcode(addressDto.zipcode())
        .country(addressDto.country())
        .build();
  }

  private Money toDomainMoney(BigDecimal price) {
    return new Money(price);
  }
}
