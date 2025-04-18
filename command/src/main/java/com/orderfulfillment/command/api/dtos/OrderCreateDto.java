package com.orderfulfillment.command.api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a request to create a new order in the system.
 *
 * <p>This DTO captures all necessary information for order creation, including customer
 * identification, ordered items, addresses, total cost, and issuance timestamp.
 *
 * @param customerId the unique identifier of the customer placing the order (must not be blank)
 * @param items the list of items to include in the order (must not be null or empty; each item must
 *     be valid)
 * @param shippingAddress the address where the order will be shipped (must not be null and must be
 *     valid)
 * @param billingAddress the address used for billing purposes (must not be null and must be valid)
 * @param totalCost the total monetary cost of the order (must not be null)
 * @param issuedAt the timestamp when the order was issued (must not be null)
 */
public record OrderCreateDto(
    @NotBlank(message = "Customer ID is required") String customerId,
    @NotNull(message = "Item is required") @Valid List<OrderItemDto> items,
    @NotNull(message = "Shipping Address is required") @Valid AddressDto shippingAddress,
    @NotNull(message = "Billing Address is required") @Valid AddressDto billingAddress,
    @NotNull(message = "Total cost is required") BigDecimal totalCost,
    @NotNull(message = "Issued timestamp is required") LocalDateTime issuedAt) {

  /**
   * Data transfer object representing a postal address.
   *
   * <p>All fields are mandatory and must not be blank.
   *
   * @param street the street address (e.g., "123 Main St"), must not be blank
   * @param city the city name, must not be blank
   * @param state the state or province name, must not be blank
   * @param zipcode the postal or ZIP code, must not be blank
   * @param country the country name, must not be blank
   */
  public record AddressDto(
      @NotBlank(message = "Street is required") String street,
      @NotBlank(message = "City is required") String city,
      @NotBlank(message = "State is required") String state,
      @NotBlank(message = "Zip code is required") String zipcode,
      @NotBlank(message = "Country is required") String country) {}

  /**
   * Data transfer object representing a single item in an order creation request. Encapsulates the
   * product identifier, quantity, and unit price for that item.
   *
   * @param productId the unique identifier of the product; must not be blank
   * @param quantity the number of units ordered; must be provided
   * @param price the unit price of the product; must be provided
   */
  public record OrderItemDto(
      @NotBlank(message = "Product ID is required") String productId,
      @NotNull(message = "quantity is required") int quantity,
      @NotNull(message = "price is required") BigDecimal price) {}
}
