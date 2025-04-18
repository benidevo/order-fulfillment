package com.orderfulfillment.command.validators.impl;

import com.orderfulfillment.command.domain.OrderStatus;
import com.orderfulfillment.command.validators.ValidOrderStatus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OrderStatusValidator implements ConstraintValidator<ValidOrderStatus, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    try {
      OrderStatus.valueOf(value);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
