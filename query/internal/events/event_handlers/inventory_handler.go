package eventhandlers

import (
	"context"
	"time"

	"github.com/benidevo/order-fulfillment/query/internal/domain"
	"github.com/benidevo/order-fulfillment/query/internal/events"
	"github.com/benidevo/order-fulfillment/query/internal/repositories"
	"github.com/benidevo/order-fulfillment/query/internal/repositories/mongodb"
)

// InventoryEventHandler handles all inventory-related events
type InventoryEventHandler struct {
	inventoryRepo repositories.InventoryRepository
}

// NewInventoryEventHandler creates a new InventoryEventHandler with the provided inventory repository.
// It returns a pointer to the initialized handler which can be used to process inventory-related events.
func NewInventoryEventHandler(inventoryRepo repositories.InventoryRepository) *InventoryEventHandler {
	return &InventoryEventHandler{
		inventoryRepo: inventoryRepo,
	}
}

// HandleInventoryUpdated processes an InventoryUpdatedEvent by either creating a new inventory item
// or updating an existing one with the latest quantity information.
func (handler *InventoryEventHandler) HandleInventoryUpdated(ctx context.Context, event *events.InventoryUpdatedEvent) error {
	inventoryItem, err := handler.inventoryRepo.FindByProductId(ctx, event.Payload.ProductID)
	if err != nil {
		if err == mongodb.ErrInventoryNotFound {
			inventoryItem = &domain.InventoryItem{
				ProductId:         event.Payload.ProductID,
				AvailableQuantity: event.Payload.Quantity,
				AllocatedQuantity: 0,
				Status:            domain.InventoryStatusAvailable,
				CreatedAt:         time.Now(),
				UpdatedAt:         time.Now(),
			}
			handler.inventoryRepo.Save(ctx, inventoryItem)
		}
		return err
	}

	inventoryItem.AvailableQuantity = event.Payload.Quantity

	if inventoryItem.AvailableQuantity > 0 && inventoryItem.Status == domain.InventoryStatusOutOfStock {
		inventoryItem.Status = domain.InventoryStatusAvailable
	} else if inventoryItem.AvailableQuantity == 0 && inventoryItem.Status == domain.InventoryStatusAvailable {
		inventoryItem.Status = domain.InventoryStatusOutOfStock
	}

	return handler.inventoryRepo.UpsertByProductId(ctx, inventoryItem)
}

// HandleInventoryAllocated processes an inventory allocation event by updating the available
// and allocated quantities for the specified product in the inventory repository.
// It decreases the available quantity and increases the allocated quantity by the amount
// specified in the event. If the available quantity reaches zero, the inventory item's
// status is updated to "Out of Stock".
func (handler *InventoryEventHandler) HandleInventoryAllocated(ctx context.Context, event *events.InventoryAllocatedEvent) error {

	inventoryItem, err := handler.inventoryRepo.FindByProductId(ctx, event.Payload.ProductID)
	if err != nil {
		return err
	}

	inventoryItem.AvailableQuantity -= event.Payload.Quantity
	inventoryItem.AllocatedQuantity += event.Payload.Quantity

	if inventoryItem.AvailableQuantity == 0 {
		inventoryItem.Status = domain.InventoryStatusOutOfStock
	}

	return handler.inventoryRepo.UpsertByProductId(ctx, inventoryItem)
}

// FindByProductId retrieves an inventory item from the repository using the product ID obtained from the event payload.
// It returns the found inventory item and any error encountered during the retrieval process.
func (handler *InventoryEventHandler) HandleInventoryReturned(ctx context.Context, event *events.InventoryReturnedEvent) error {
	inventoryItem, err := handler.inventoryRepo.FindByProductId(ctx, event.Payload.ProductID)
	if err != nil {
		return err
	}

	inventoryItem.AllocatedQuantity -= event.Payload.Quantity
	inventoryItem.AvailableQuantity += event.Payload.Quantity

	if inventoryItem.Status == domain.InventoryStatusOutOfStock && inventoryItem.AvailableQuantity > 0 {
		inventoryItem.Status = domain.InventoryStatusAvailable
	}

	return handler.inventoryRepo.UpsertByProductId(ctx, inventoryItem)
}
