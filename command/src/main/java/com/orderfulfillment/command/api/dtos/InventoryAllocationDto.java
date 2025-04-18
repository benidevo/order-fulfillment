package com.orderfulfillment.command.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for allocating inventory to an order.
 *
 * <p>This DTO contains the order ID and the quantity to be allocated.
 *
 * @param orderId the unique identifier of the order
 * @param quantity the quantity to be allocated
 */
public record InventoryAllocationDto(
    @NotBlank(message = "Order ID is required") String orderId,
    @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be positive")
        Integer quantity) {}
