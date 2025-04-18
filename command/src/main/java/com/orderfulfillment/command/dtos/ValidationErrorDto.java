package com.orderfulfillment.command.dtos;

import java.util.HashMap;
import lombok.Builder;

@Builder
public record ValidationErrorDto(boolean success, HashMap<String, String> errors) {}
