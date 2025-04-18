package com.orderfulfillment.command.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderfulfillment.command.dtos.OrderCreateDto;
import com.orderfulfillment.command.dtos.OrderStatusUpdateDto;
import com.orderfulfillment.command.dtos.ResponseDto;

@RestController
@RequestMapping(value = "/api/v1/orders")
public class OrdersController {

  @PostMapping
  public ResponseEntity<ResponseDto> registerOrder(@Valid @RequestBody OrderCreateDto orderDto) {
    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }

  @DeleteMapping(value = "/{orderId}")
  public ResponseEntity<ResponseDto> cancelOrder(@PathVariable String orderId) {
    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }

  @PutMapping(value = "/{orderId}/status")
  public ResponseEntity<ResponseDto> updateOrderStatus(
      @PathVariable String orderId, @Valid @RequestBody OrderStatusUpdateDto status) {
    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }
}
