package com.orderfulfillment.command.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = com.orderfulfillment.command.validators.impl.OrderStatusValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOrderStatus {
  String message() default "Invalid order status";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
