package models

import (
	"fmt"
	"time"

	"github.com/benidevo/order-fulfillment/query/internal/domain"
	"github.com/shopspring/decimal"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

const (
	OrderCollectionName = "orders"
)

// OrderItemModel represents an item in an order in the MongoDB storage
type OrderItemModel struct {
	ProductId string     `bson:"productId"`
	Quantity  int        `bson:"quantity"`
	Price     MoneyModel `bson:"price"`
}

// MoneyModel represents a monetary value with currency in the MongoDB storage
type MoneyModel struct {
	Currency string          `bson:"currency"`
	Value    decimal.Decimal `bson:"value"`
}

// AddressModel represents a geographical location with detailed information
type AddressModel struct {
	Street  string `bson:"street"`
	City    string `bson:"city"`
	State   string `bson:"state"`
	ZipCode string `bson:"zipCode"`
	Country string `bson:"country"`
}

// OrderModel represents an order in the MongoDB storage
type OrderModel struct {
	Id              primitive.ObjectID `bson:"_id"`
	OrderId         string             `bson:"orderId"`
	CustomerId      string             `bson:"customerId"`
	Items           []OrderItemModel   `bson:"items"`
	Status          string             `bson:"status"`
	ShippingAddress AddressModel       `bson:"shippingAddress"`
	BillingAddress  AddressModel       `bson:"billingAddress"`
	TotalCost       MoneyModel         `bson:"totalCost"`
	IssuedAt        time.Time          `bson:"issuedAt"`
	CreatedAt       time.Time          `bson:"createdAt"`
	UpdatedAt       time.Time          `bson:"updatedAt"`
}

// OrderIndexes defines the MongoDB indexes for the Order collection.
// It includes:
// - A unique index on the 'orderId' field to ensure no duplicate orders.
// - An index on 'customerId' to optimize queries filtering by customer.
// - An index on 'status' to optimize queries filtering by order status.
var OrderIndexes = []mongo.IndexModel{
	{
		Keys:    bson.D{{Key: "orderId", Value: 1}},
		Options: options.Index().SetUnique(true),
	},
	{
		Keys:    bson.D{{Key: "customerId", Value: 1}},
		Options: options.Index(),
	},
	{
		Keys:    bson.D{{Key: "status", Value: 1}},
		Options: options.Index(),
	},
}

// ToDomain converts an OrderModel to a domain.Order.
// It transforms all the nested fields like items, addresses, and money values
// to their domain equivalents.
func (o *OrderModel) ToDomain() (*domain.Order, error) {
	status, err := domain.OrderStatusFromString(o.Status)
	if err != nil {
		return nil, fmt.Errorf("invalid order status: %w", err)
	}

	items := make([]domain.OrderItem, len(o.Items))
	for i, item := range o.Items {
		items[i] = domain.OrderItem{
			ProductId: item.ProductId,
			Quantity:  item.Quantity,
			Price: domain.Money{
				Currency: item.Price.Currency,
				Value:    item.Price.Value,
			},
		}
	}

	shippingAddress := domain.Address{
		Street:  o.ShippingAddress.Street,
		City:    o.ShippingAddress.City,
		State:   o.ShippingAddress.State,
		ZipCode: o.ShippingAddress.ZipCode,
		Country: o.ShippingAddress.Country,
	}

	billingAddress := domain.Address{
		Street:  o.BillingAddress.Street,
		City:    o.BillingAddress.City,
		State:   o.BillingAddress.State,
		ZipCode: o.BillingAddress.ZipCode,
		Country: o.BillingAddress.Country,
	}

	return &domain.Order{
		Id:              o.Id.Hex(),
		OrderId:         o.OrderId,
		CustomerId:      o.CustomerId,
		Items:           items,
		Status:          status,
		ShippingAddress: shippingAddress,
		BillingAddress:  billingAddress,
		TotalCost: domain.Money{
			Currency: o.TotalCost.Currency,
			Value:    o.TotalCost.Value,
		},
		IssuedAt:  o.IssuedAt,
		CreatedAt: o.CreatedAt,
		UpdatedAt: o.UpdatedAt,
	}, nil
}

// OrderModelFromDomain converts a domain Order object to an OrderModel for database storage.
func OrderModelFromDomain(o *domain.Order) (*OrderModel, error) {
	var id primitive.ObjectID
	if o.Id != "" {
		var err error
		id, err = primitive.ObjectIDFromHex(o.Id)
		if err != nil {
			return nil, fmt.Errorf("invalid order ID: %w", err)
		}
	} else {
		id = primitive.NewObjectID()
	}

	items := make([]OrderItemModel, len(o.Items))
	for i, item := range o.Items {
		items[i] = OrderItemModel{
			ProductId: item.ProductId,
			Quantity:  item.Quantity,
			Price: MoneyModel{
				Currency: item.Price.Currency,
				Value:    item.Price.Value,
			},
		}
	}

	shippingAddress := AddressModel{
		Street:  o.ShippingAddress.Street,
		City:    o.ShippingAddress.City,
		State:   o.ShippingAddress.State,
		ZipCode: o.ShippingAddress.ZipCode,
		Country: o.ShippingAddress.Country,
	}

	billingAddress := AddressModel{
		Street:  o.BillingAddress.Street,
		City:    o.BillingAddress.City,
		State:   o.BillingAddress.State,
		ZipCode: o.BillingAddress.ZipCode,
		Country: o.BillingAddress.Country,
	}

	return &OrderModel{
		Id:              id,
		OrderId:         o.OrderId,
		CustomerId:      o.CustomerId,
		Items:           items,
		Status:          o.Status.String(),
		ShippingAddress: shippingAddress,
		BillingAddress:  billingAddress,
		TotalCost: MoneyModel{
			Currency: o.TotalCost.Currency,
			Value:    o.TotalCost.Value,
		},
		IssuedAt:  o.IssuedAt,
		CreatedAt: o.CreatedAt,
		UpdatedAt: o.UpdatedAt,
	}, nil
}
