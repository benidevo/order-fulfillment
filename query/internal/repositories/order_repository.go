package repositories

import (
	"context"

	"github.com/benidevo/order-fulfillment/query/internal/domain"
)

// OrderRepository defines operations for managing orders in storage.
// The repository abstracts the persistence mechanism and provides a clean
// interface for storage operations related to Order domain objects.
type OrderRepository interface {
	// FindById retrieves an order by its unique identifier.
	FindById(ctx context.Context, id string) (*domain.Order, error)

	// FindAll retrieves all orders from the storage.
	FindAll(ctx context.Context) ([]*domain.Order, error)

	// FindByCustomerId retrieves all orders associated with a specific customer.
	FindByCustomerId(ctx context.Context, customerId string) ([]*domain.Order, error)

	// FindByStatus retrieves all orders with a specific status.
	FindByStatus(ctx context.Context, status string) ([]*domain.Order, error)

	// Save persists a new order into the storage.
	Save(ctx context.Context, order *domain.Order) error

	// UpsertByOrderId updates an order in the storage if it exists, or inserts it if it doesn't exist.
	UpsertByOrderId(ctx context.Context, order *domain.Order) error
}
