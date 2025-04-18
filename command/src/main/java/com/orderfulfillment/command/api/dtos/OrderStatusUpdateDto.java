package com.orderfulfillment.command.api.dtos;

import com.orderfulfillment.command.annotations.ValidOrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for updating an order's status.
 *
 * <p>This record encapsulates the new status for an existing order. The {@code status} field is
 * mandatory and validated against the set of allowed order statuses defined by the
 * {@code @ValidOrderStatus} constraint.
 *
 * @param status the new order status; must be non-null and conform to valid order status values
 */
public record OrderStatusUpdateDto(@NotNull @ValidOrderStatus String status) {}
