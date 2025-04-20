package events

// OrderCreatedPayload represents the payload when a new order is created
type OrderCreatedPayload struct {
	OrderId         string         `json:"orderId"`
	CustomerId      string         `json:"customerId"`
	Items           []OrderItemDto `json:"items"`
	Status          string         `json:"status"`
	ShippingAddress AddressDto     `json:"shippingAddress"`
	BillingAddress  AddressDto     `json:"billingAddress"`
	TotalCost       MoneyDto       `json:"totalCost"`
	IssuedAt        string         `json:"issuedAt"`
}

// OrderStatusUpdatedPayload represents the payload when an order's status is updated
type OrderStatusUpdatedPayload struct {
	OrderId string `json:"orderId"`
	Status  string `json:"status"`
}

// OrderCancelledPayload represents the payload when an order is cancelled
type OrderCancelledPayload struct {
	OrderId string `json:"orderId"`
}

// OrderItemDto represents an item in an order with product details
type OrderItemDto struct {
	ProductId string   `json:"productId"`
	Quantity  int      `json:"quantity"`
	Price     MoneyDto `json:"price"`
}

// AddressDto represents a geographical location with detailed information
type AddressDto struct {
	Street  string `json:"street"`
	City    string `json:"city"`
	State   string `json:"state"`
	ZipCode string `json:"zipCode"`
	Country string `json:"country"`
}

// MoneyDto represents a monetary value with currency
type MoneyDto struct {
	Currency string `json:"currency"`
	Value    string `json:"value"`
}

// OrderCreatedEvent represents the event that occurs when a new order is created.
// It extends the BaseEvent struct and includes a payload specific to order creation.
type OrderCreatedEvent struct {
	BaseEvent
	Payload OrderCreatedPayload
}

// OrderStatusUpdatedEvent represents the event that occurs when an order's status is updated.
// It extends the BaseEvent struct and includes a payload specific to order status updates.
type OrderStatusUpdatedEvent struct {
	BaseEvent
	Payload OrderStatusUpdatedPayload
}

// OrderCancelledEvent represents the event that occurs when an order is cancelled.
// It extends the BaseEvent struct and includes a payload specific to order cancellation.
type OrderCancelledEvent struct {
	BaseEvent
	Payload OrderCancelledPayload
}
