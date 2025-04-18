package com.orderfulfillment.command.dtos;

import lombok.Builder;

@Builder
public record ResponseDto(boolean success, Object data) {}
