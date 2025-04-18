package com.orderfulfillment.command.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for updating inventory.
 *
 * <p>This DTO contains the new quantity for the product.
 *
 * @param quantity the new available quantity
 */
public record InventoryUpdateDto(
    @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be non-negative")
        Integer quantity) {}
