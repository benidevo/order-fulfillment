package com.orderfulfillment.command.api.dtos;

import java.util.HashMap;
import lombok.Builder;

/**
 * Data Transfer Object representing the outcome of a validation operation.
 *
 * <p>Holds a flag indicating whether the operation was successful and, if not, a map of field names
 * to corresponding error messages.
 *
 * @param success true if validation passed without errors, false otherwise
 * @param errors a map where each key is a field name and each value is the associated error message
 */
@Builder
public record ValidationErrorDto(boolean success, HashMap<String, String> errors) {}
