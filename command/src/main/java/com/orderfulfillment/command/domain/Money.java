package com.orderfulfillment.command.domain;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a monetary amount with a fixed currency (EUR). Immutable value object that
 * encapsulates an amount and its currency.
 */
@Getter
@ToString
@EqualsAndHashCode
public class Money {
  private final BigDecimal value;
  private final String currency;

  public Money(BigDecimal value) {
    this.value = value;
    this.currency = "EUR";
  }

  /**
   * Creates a new Money instance representing this amount multiplied by the given quantity.
   *
   * @param quantity The quantity to multiply by
   * @return A new Money instance with the multiplied amount and same currency
   */
  public Money multiply(int quantity) {
    return new Money(value.multiply(BigDecimal.valueOf(quantity)));
  }
}
