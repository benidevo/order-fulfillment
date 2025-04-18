package com.orderfulfillment.command.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a postal address.
 *
 * <p>Encapsulates the street, city, state, zipcode, and country.
 *
 * @param street the street address
 * @param city the city
 * @param state the state or province
 * @param zipcode the postal code
 * @param country the country
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Address {
  private final String street;
  private final String city;
  private final String state;
  private final String zipcode;
  private final String country;
}
