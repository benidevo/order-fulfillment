package com.orderfulfillment.command.api;

import com.orderfulfillment.command.api.dtos.InventoryAllocationDto;
import com.orderfulfillment.command.api.dtos.InventoryUpdateDto;
import com.orderfulfillment.command.api.dtos.ResponseDto;
import com.orderfulfillment.command.commands.AllocateInventoryCommand;
import com.orderfulfillment.command.commands.ReturnInventoryCommand;
import com.orderfulfillment.command.commands.UpdateInventoryCommand;
import com.orderfulfillment.command.handlers.InventoryCommandHandler;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/inventory")
public class InventoryApi {
  private final InventoryCommandHandler inventoryCommandHandler;

  public InventoryApi(InventoryCommandHandler inventoryCommandHandler) {
    this.inventoryCommandHandler = inventoryCommandHandler;
  }

  /**
   * Updates the inventory quantity for a product.
   *
   * @param productId the unique identifier of the product
   * @param updateDto the DTO containing the new quantity
   * @return a ResponseEntity indicating success or failure
   */
  @PutMapping("/{productId}")
  public ResponseEntity<ResponseDto> updateInventory(
      @PathVariable String productId, @Valid @RequestBody InventoryUpdateDto updateDto) {

    UpdateInventoryCommand command =
        UpdateInventoryCommand.builder()
            .productId(productId)
            .quantity(updateDto.quantity())
            .build();

    log.info("Updating inventory for product {}", productId);
    inventoryCommandHandler.handle(command);

    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }

  /**
   * Allocates inventory from a product to an order.
   *
   * @param productId the unique identifier of the product
   * @param allocationDto the DTO containing order ID and quantity
   * @return a ResponseEntity indicating success or failure
   */
  @PostMapping("/{productId}/allocate")
  public ResponseEntity<ResponseDto> allocateInventory(
      @PathVariable String productId, @Valid @RequestBody InventoryAllocationDto allocationDto) {

    AllocateInventoryCommand command =
        AllocateInventoryCommand.builder()
            .productId(productId)
            .orderId(allocationDto.orderId())
            .quantity(allocationDto.quantity())
            .build();

    log.info("Allocating inventory for product {} to order {}", productId, allocationDto.orderId());
    inventoryCommandHandler.handle(command);

    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }

  /**
   * Returns previously allocated inventory back to available stock.
   *
   * @param productId the unique identifier of the product
   * @param allocationDto the DTO containing order ID and quantity
   * @return a ResponseEntity indicating success or failure
   */
  @PostMapping("/{productId}/return")
  public ResponseEntity<ResponseDto> returnInventory(
      @PathVariable String productId, @Valid @RequestBody InventoryAllocationDto allocationDto) {

    ReturnInventoryCommand command =
        ReturnInventoryCommand.builder()
            .productId(productId)
            .orderId(allocationDto.orderId())
            .quantity(allocationDto.quantity())
            .build();

    log.info(
        "Returning inventory for product {} from order {}", productId, allocationDto.orderId());
    inventoryCommandHandler.handle(command);

    var response = ResponseDto.builder().success(true).build();
    return ResponseEntity.ok().body(response);
  }
}
