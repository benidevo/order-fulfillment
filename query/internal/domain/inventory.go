package domain

import (
	"fmt"
	"time"
)

// InventoryStatus represents the state of an inventory item
type InventoryStatus int

const (
	InventoryStatusAvailable InventoryStatus = iota
	InventoryStatusOutOfStock
	InventoryStatusDiscontinued
)

// Returns the string representation of the inventory status
func (s InventoryStatus) String() string {
	switch s {
	case InventoryStatusAvailable:
		return "AVAILABLE"
	case InventoryStatusOutOfStock:
		return "OUT_OF_STOCK"
	case InventoryStatusDiscontinued:
		return "DISCONTINUED"
	default:
		return "UNKNOWN"
	}
}

// Converts a string representation of an inventory status
// to its corresponding InventoryStatus enum value.
// Valid status values are "AVAILABLE", "OUT_OF_STOCK", and "DISCONTINUED".
// Returns an error if the provided status string is not valid.
func InventoryStatusFromString(status string) (InventoryStatus, error) {
	switch status {
	case "AVAILABLE":
		return InventoryStatusAvailable, nil
	case "OUT_OF_STOCK":
		return InventoryStatusOutOfStock, nil
	case "DISCONTINUED":
		return InventoryStatusDiscontinued, nil
	default:
		return -1, fmt.Errorf("invalid inventory status: %s", status)
	}
}

// InventoryItem represents stock for a specific product
type InventoryItem struct {
	Id                string
	ProductId         string
	AvailableQuantity int
	AllocatedQuantity int
	Status            InventoryStatus
	CreatedAt         time.Time
	UpdatedAt         time.Time
}

// Creates a new inventory item with the given id, product id, and available quantity.
// It returns an error if the available quantity is negative.
// The status is set to InventoryStatusAvailable by default, or InventoryStatusOutOfStock if the available quantity is zero.
// The allocated quantity is initialized to 0, and both created and updated timestamps are set to the current time.
func NewInventoryItem(id string, productId string, availableQuantity int) (*InventoryItem, error) {
	if availableQuantity < 0 {
		return nil, fmt.Errorf("available quantity cannot be negative")
	}

	status := InventoryStatusAvailable
	if availableQuantity == 0 {
		status = InventoryStatusOutOfStock
	}

	now := time.Now()
	return &InventoryItem{
		Id:                id,
		ProductId:         productId,
		AvailableQuantity: availableQuantity,
		AllocatedQuantity: 0,
		Status:            status,
		CreatedAt:         now,
		UpdatedAt:         now,
	}, nil
}

// Decreases the available quantity and increases the allocated quantity by the specified amount.
// It returns an error if the requested quantity is invalid (<=0) or exceeds available inventory.
// The method also updates the item timestamp and sets the status to OutOfStock when appropriate.
//
// Parameters:
//   - quantity: The number of items to allocate (must be positive)
//
// Returns:
//   - error: nil if allocation succeeded, otherwise an error describing why it failed
func (i *InventoryItem) Allocate(quantity int) error {
	if quantity <= 0 {
		return fmt.Errorf("allocation quantity must be positive")
	}

	if quantity > i.AvailableQuantity {
		return fmt.Errorf("insufficient inventory: requested %d, available %d",
			quantity, i.AvailableQuantity)
	}

	i.AvailableQuantity -= quantity
	i.AllocatedQuantity += quantity
	i.UpdatedAt = time.Now()

	if i.AvailableQuantity == 0 {
		i.Status = InventoryStatusOutOfStock
	}

	return nil
}

// Releases the specified quantity from allocated to available inventory.
// It increases the available quantity and decreases the allocated quantity by the given amount.
//
// Parameters:
//   - quantity: The amount to deallocate, must be positive and not exceed allocated quantity
//
// Returns:
//   - error: If quantity is not positive or exceeds the currently allocated amount
func (i *InventoryItem) Deallocate(quantity int) error {
	if quantity <= 0 {
		return fmt.Errorf("deallocation quantity must be positive")
	}

	if quantity > i.AllocatedQuantity {
		return fmt.Errorf("cannot deallocate more than allocated: requested %d, allocated %d",
			quantity, i.AllocatedQuantity)
	}

	i.AllocatedQuantity -= quantity
	i.AvailableQuantity += quantity
	i.UpdatedAt = time.Now()

	if i.Status == InventoryStatusOutOfStock && i.AvailableQuantity > 0 {
		i.Status = InventoryStatusAvailable
	}

	return nil
}

// Increases the available quantity of the inventory item by the specified amount.
// It returns an error if the quantity to add is not positive.
//
// Parameters:
//   - quantity: The amount of stock to add (must be positive)
//
// Returns:
//   - error: An error if the quantity is not positive, nil otherwise
func (i *InventoryItem) AddStock(quantity int) error {

	if quantity <= 0 {
		return fmt.Errorf("added quantity must be positive")
	}

	i.AvailableQuantity += quantity
	i.UpdatedAt = time.Now()

	if i.Status == InventoryStatusOutOfStock && i.AvailableQuantity > 0 {
		i.Status = InventoryStatusAvailable
	}

	return nil
}

// Returns the total inventory (available + allocated)
func (i *InventoryItem) GetTotalQuantity() int {
	return i.AvailableQuantity + i.AllocatedQuantity
}

// Checks if there's enough available inventory
func (i *InventoryItem) HasSufficientQuantity(quantity int) bool {
	return quantity <= i.AvailableQuantity
}

// Marks the product as discontinued
func (i *InventoryItem) Discontinue() {
	i.Status = InventoryStatusDiscontinued
	i.UpdatedAt = time.Now()
}
