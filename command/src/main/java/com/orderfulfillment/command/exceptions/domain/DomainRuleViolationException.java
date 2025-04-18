package com.orderfulfillment.command.exceptions.domain;

/**
 * Base exception for all domain rule violations. Used when an operation violates a business rule
 * defined in the domain.
 */
public abstract class DomainRuleViolationException extends OrderFulfillmentException {
  public DomainRuleViolationException(String message) {
    super(message);
  }

  public DomainRuleViolationException(String message, Throwable cause) {
    super(message, cause);
  }
}
