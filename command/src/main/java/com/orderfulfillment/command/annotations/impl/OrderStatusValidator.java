package com.orderfulfillment.command.annotations.impl;

import com.orderfulfillment.command.annotations.ValidOrderStatus;
import com.orderfulfillment.command.domain.OrderStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation that checks whether a given String corresponds to a valid {@link
 * OrderStatus} enum constant.
 *
 * <p>Null values are considered valid because they wil be handled by {@code @NotNull}. Non-null
 * values are validated by attempting to convert the input String to an {@code OrderStatus} using
 * {@link OrderStatus#valueOf(String)}. If the conversion succeeds, the value is valid; otherwise,
 * it is invalid.
 *
 * @author
 * @see ValidOrderStatus
 * @see OrderStatus
 */
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
