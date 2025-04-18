package com.orderfulfillment.command.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreateDto(
    @NotBlank(message = "Customer ID is required") String customerId,
    @NotNull(message = "Item is required") List<OrderItemDto> items,
    @NotNull(message = "Shipping Address is required") AddressDto shippingAddress,
    @NotNull(message = "Billing Address is required") AddressDto billingAddress,
    @NotNull(message = "Total cost is required") BigDecimal totalCost,
    @NotNull(message = "Issued timestamp is required") LocalDateTime issuedAt) {

  public record AddressDto(
      @NotBlank(message = "Street is required") String street,
      @NotBlank(message = "City is required") String city,
      @NotBlank(message = "State is required") String state,
      @NotBlank(message = "Zip code is required") String zipCode,
      @NotNull(message = "Country is required") String country) {}

  public record OrderItemDto(
      @NotBlank(message = "Product ID is required") String productID,
      @NotNull(message = "quantity is required") int quantity,
      @NotNull(message = "price is required") BigDecimal price) {}
}
