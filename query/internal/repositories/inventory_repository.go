package repositories

import (
	"context"

	"github.com/benidevo/order-fulfillment/query/internal/domain"
)

// InventoryRepository defines operations for managing inventory items in storage.
type InventoryRepository interface {
	// FindById retrieves an inventory item by its unique identifier.
	// Returns nil and an error if the item cannot be found or an error occurs.
	FindById(ctx context.Context, id string) (*domain.InventoryItem, error)

	// FindByProductId retrieves an inventory item by its product ID.
	// Returns nil and an error if the item cannot be found or an error occurs.
	FindByProductId(ctx context.Context, productId string) (*domain.InventoryItem, error)

	// FindAll retrieves all inventory items.
	// Returns an empty slice and an error if an error occurs.
	FindAll(ctx context.Context) ([]*domain.InventoryItem, error)

	// Save persists a new inventory item or updates an existing one.
	// Returns an error if the operation fails.
	Save(ctx context.Context, item *domain.InventoryItem) error

	// UpsertByProductId creates or updates an inventory item based on its product ID.
	// Returns an error if the operation fails.
	UpsertByProductId(ctx context.Context, item *domain.InventoryItem) error
}
