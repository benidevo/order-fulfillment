package handlers

// Handlers holds all the HTTP request handlers for the application
type Handlers struct {
	Inventory *InventoryHandler
	Orders    *OrdersHandler
}
