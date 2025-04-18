package com.orderfulfillment.command.api.dtos;

import lombok.Builder;

/**
 * A data transfer object representing a standard API response.
 *
 * <p>Contains a flag indicating whether the operation was successful, and an optional payload
 * carrying additional data.
 *
 * @param success true if the operation completed successfully, false otherwise
 * @param data the response payload, can be any type or null if there is no data
 */
@Builder
public record ResponseDto(boolean success, Object data) {}
