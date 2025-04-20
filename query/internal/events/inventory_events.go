package events

// InventoryUpdatedEvent represents the payload when inventory is updated
type InventoryUpdatedPayload struct {
	ProductID string `json:"productId"`
	Quantity  int    `json:"quantity"`
}

// InventoryUpdatedEvent represents the event that occurs when an inventory is updated.
// It extends the BaseEvent struct and includes a payload specific to inventory updates.
type InventoryUpdatedEvent struct {
	BaseEvent
	Payload InventoryUpdatedPayload
}

// InventoryAllocatedPayload represents the payload when inventory is allocated
type InventoryAllocatedPayload struct {
	ProductID string `json:"productId"`
	OrderID   string `json:"orderId"`
	Quantity  int    `json:"quantity"`
}

// InventoryAllocatedEvent represents an event indicating that inventory has been allocated.
// It extends the BaseEvent and contains payload with information about the allocated inventory.
type InventoryAllocatedEvent struct {
	BaseEvent
	Payload InventoryAllocatedPayload
}

// InventoryReturnedPayload represents the payload when inventory is returned
type InventoryReturnedPayload struct {
	ProductID string `json:"productId"`
	OrderID   string `json:"orderId"`
	Quantity  int    `json:"quantity"`
}

// InventoryReturnedEvent represents an event indicating that inventory has been returned.
// It extends the BaseEvent and contains payload with information about the returned inventory.
type InventoryReturnedEvent struct {
	BaseEvent
	Payload InventoryReturnedPayload
}
