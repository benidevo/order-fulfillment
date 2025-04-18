package com.orderfulfillment.command.dtos;

import com.orderfulfillment.command.validators.ValidOrderStatus;

import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDto(@NotNull @ValidOrderStatus String status) {}
