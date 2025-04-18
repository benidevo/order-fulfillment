package com.orderfulfillment.command.exceptions;

/**
 * Exception thrown when a concurrency conflict occurs during the save operation of an Order
 * aggregate.
 *
 * <p>This exception is typically used to indicate that the version of the Order being saved does
 * not match the expected version in the event store, preventing the save operation from completing.
 */
public class ConcurrencyException extends RuntimeException {

  private final String aggregateId;
  private final long expectedVersion;
  private final long actualVersion;

  public ConcurrencyException(String aggregateId, long expectedVersion, long actualVersion) {
    super(
        String.format(
            "Concurrency conflict for aggregate %s: expected version %d, but found %d",
            aggregateId, expectedVersion, actualVersion));
    this.aggregateId = aggregateId;
    this.expectedVersion = expectedVersion;
    this.actualVersion = actualVersion;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public long getExpectedVersion() {
    return expectedVersion;
  }

  public long getActualVersion() {
    return actualVersion;
  }
}
