package domain

import (
	"fmt"
	"time"

	"github.com/shopspring/decimal"
)

// Money represents a monetary value with a specified currency.
// It holds both the currency code (e.g., "USD", "EUR") and the actual value
// as a high-precision decimal to avoid floating-point errors in financial calculations.
type Money struct {
	Currency string
	Value    decimal.Decimal
}

// Creates a new Money value with the specified currency and decimal value.
// The currency should be a valid currency code (e.g., "USD", "EUR").
// The value should be a decimal representation of the money amount.
func NewMoney(currency string, value decimal.Decimal) Money {
	return Money{
		Currency: currency,
		Value:    value,
	}
}

// OrderItem represents a line item in an order, containing details about a product being purchased.
// It includes the product identifier, quantity ordered, and the price of the item.
type OrderItem struct {
	ProductId string
	Quantity  int
	Price     Money
}

// Creates a new order item with the specified product Id, quantity, and price.
// It returns an error if the quantity is less than or equal to zero.
// The returned OrderItem contains the product Id, quantity, and price provided.
func NewOrderItem(productId string, quantity int, price Money) (OrderItem, error) {
	if quantity <= 0 {
		return OrderItem{}, fmt.Errorf("quantity must be greater than zero")
	}
	return OrderItem{
		ProductId: productId,
		Quantity:  quantity,
		Price:     price,
	}, nil
}

// Address represents a geographical location with detailed information about street, city, state,
// postal code, and country. It is used for shipping and billing purposes in orders.
type Address struct {
	Street  string
	City    string
	State   string
	ZipCode string
	Country string
}

// Creates a new Address instance with the given street, city, state, zipCode, and country.
// Each parameter corresponds to its respective field in the Address struct.
// Returns a fully initialized Address struct.
func NewAddress(street, city, state, zipCode, country string) Address {
	return Address{
		Street:  street,
		City:    city,
		State:   state,
		ZipCode: zipCode,
		Country: country,
	}
}

// OrderStatus represents the status of an order in the system.
type OrderStatus int

const (
	OrderStatusRegistered OrderStatus = iota
	OrderStatusCancelled
	OrderStatusShipped
	OrderStatusPartiallyShipped
	OrderStatusDelivered
	OrderStatusPartiallyDelivered
)

// Returns the string representation of the order status
func (s OrderStatus) String() string {
	switch s {
	case OrderStatusRegistered:
		return "REGISTERED"
	case OrderStatusCancelled:
		return "CANCELLED"
	case OrderStatusShipped:
		return "SHIPPED"
	case OrderStatusPartiallyShipped:
		return "PARTIALLY_SHIPPED"
	case OrderStatusDelivered:
		return "DELIVERED"
	case OrderStatusPartiallyDelivered:
		return "PARTIALLY_DELIVERED"
	default:
		return "UNKNOWN"
	}
}

// Converts a string representation of an order status to an OrderStatus enum value.
// It accepts case-sensitive status strings such as "REGISTERED", "CANCELLED", "SHIPPED", etc.
// Returns the corresponding OrderStatus value if the string matches a known status,
// or returns -1 and an error if the status string is invalid.
func OrderStatusFromString(status string) (OrderStatus, error) {
	switch status {
	case "REGISTERED":
		return OrderStatusRegistered, nil
	case "CANCELLED":
		return OrderStatusCancelled, nil
	case "SHIPPED":
		return OrderStatusShipped, nil
	case "PARTIALLY_SHIPPED":
		return OrderStatusPartiallyShipped, nil
	case "DELIVERED":
		return OrderStatusDelivered, nil
	case "PARTIALLY_DELIVERED":
		return OrderStatusPartiallyDelivered, nil
	default:
		return -1, fmt.Errorf("invalid order status: %s", status)
	}
}

// Represents an order within the system.
// It contains all relevant information about an order including customer details,
// order items, shipping and billing information, and various timestamps.
//
// An Order is uniquely identified by its Id and is associated with a specific CustomerId.
// It tracks the current Status of the order lifecycle and includes both ShippingAddress
// and BillingAddress information.
//
// The TotalCost field represents the aggregated cost of all items in the order.
// The IssuedAt timestamp indicates when the order was officially issued to the customer.
// CreatedAt and UpdatedAt timestamps track the order record's lifecycle in the system.
type Order struct {
	Id              string
	OrderId         string
	CustomerId      string
	Items           []OrderItem
	Status          OrderStatus
	ShippingAddress Address
	BillingAddress  Address
	TotalCost       Money
	IssuedAt        time.Time
	CreatedAt       time.Time
	UpdatedAt       time.Time
}

// Creates a new order with the provided details.
// It returns a pointer to a new Order struct with the given parameters and
// initializes the Status field to OrderStatusRegistered and timestamps to the current time.
//
// Parameters:
//   - orderId: unique identifier for the order
//   - customerId: identifier of the customer placing the order
//   - items: slice of OrderItem representing products in the order
//   - shippingAddress: delivery address for the order
//   - billingAddress: address used for billing purposes
//   - issuedAt: time when the order was issued
//
// Returns:
//   - *Order: pointer to the newly created Order
func NewOrder(orderId string, customerId string, items []OrderItem, shippingAddress Address, billingAddress Address, issuedAt time.Time) *Order {

	return &Order{
		Id:              orderId,
		CustomerId:      customerId,
		Items:           items,
		Status:          OrderStatusRegistered,
		ShippingAddress: shippingAddress,
		BillingAddress:  billingAddress,
		IssuedAt:        issuedAt,
		CreatedAt:       time.Now(),
		UpdatedAt:       time.Now(),
	}
}

// Checks if the order can be cancelled.
// An order can only be cancelled if it's in the 'Registered' status.
// Returns true if the order can be cancelled, false otherwise.
func (o *Order) CanBeCancelled() bool {
	return o.Status == OrderStatusRegistered
}

// Checks if the order is eligible for shipping.
// Returns true if the order's status is 'OrderStatusRegistered',
// indicating it has been registered and is ready to be shipped.
func (o *Order) CanBeShipped() bool {
	return o.Status == OrderStatusRegistered
}

// Checks if the order is in a state where it can be delivered.
// Returns true if the order status is either "shipped" or "partially shipped".
func (o *Order) CanBeDelivered() bool {
	return o.Status == OrderStatusShipped || o.Status == OrderStatusPartiallyShipped
}
