package com.orderfulfillment.command.api.dtos;

import java.util.Map;
import lombok.Builder;

/**
 * A standardized error response DTO for API error responses.
 *
 * @param success always false for error responses
 * @param error an error code identifying the type of error
 * @param message a human-readable error message
 * @param details additional error details specific to the error type
 */
@Builder
public record ErrorResponseDto(
    boolean success, String error, String message, Map<String, Object> details) {}
