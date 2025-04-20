package main

import (
	"math/big"
	"time"
)

// ResponseDto represents the standard API response
type ResponseDto struct {
	Success bool        `json:"success"`
	Data    interface{} `json:"data,omitempty"`
}

// InventoryUpdateDto represents the DTO for updating inventory
type InventoryUpdateDto struct {
	Quantity int `json:"quantity"`
}

// InventoryAllocationDto represents the DTO for allocating inventory
type InventoryAllocationDto struct {
	OrderId  string `json:"orderId"`
	Quantity int    `json:"quantity"`
}

// OrderCreateDto represents the DTO for creating an order
type OrderCreateDto struct {
	CustomerId      string         `json:"customerId"`
	Items           []OrderItemDto `json:"items"`
	ShippingAddress AddressDto     `json:"shippingAddress"`
	BillingAddress  AddressDto     `json:"billingAddress"`
	TotalCost       *big.Float     `json:"totalCost"`
	IssuedAt        time.Time      `json:"issuedAt"`
}

// OrderItemDto represents an item in an order
type OrderItemDto struct {
	ProductId string     `json:"productId"`
	Quantity  int        `json:"quantity"`
	Price     *big.Float `json:"price"`
}

// AddressDto represents a postal address
type AddressDto struct {
	Street  string `json:"street"`
	City    string `json:"city"`
	State   string `json:"state"`
	Zipcode string `json:"zipcode"`
	Country string `json:"country"`
}

// OrderStatusUpdateDto represents the DTO for updating an order's status
type OrderStatusUpdateDto struct {
	Status string `json:"status"`
}

// Product represents a product with its details
type Product struct {
	ID    string
	Name  string
	Price *big.Float
	Stock int
}

// Customer represents a customer with their details
type Customer struct {
	ID      string
	Name    string
	Address AddressDto
}

// Order represents an order with its details and current status
type Order struct {
	ID              string
	CustomerID      string
	Items           []OrderItemDto
	Status          string
	ShippingAddress AddressDto
	BillingAddress  AddressDto
	TotalCost       *big.Float
	IssuedAt        time.Time
}
