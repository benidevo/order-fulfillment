package mongodb

import (
	"context"
	"time"

	"github.com/benidevo/order-fulfillment/query/internal/domain"
	"github.com/benidevo/order-fulfillment/query/internal/models"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

// mongoOrderRepository defines operations for managing orders in storage.
type mongoOrderRepository struct {
	collection *mongo.Collection
}

// NewMongoOrderRepository creates a new instance of mongoOrderRepository.
func NewMongoOrderRepository(db *mongo.Database) *mongoOrderRepository {
	return &mongoOrderRepository{
		collection: db.Collection("orders"),
	}
}

// FindById retrieves an order by its ID from the MongoDB collection.
func (m *mongoOrderRepository) FindById(ctx context.Context, id string) (*domain.Order, error) {
	filter := bson.M{"orderId": id}

	var orderModel models.OrderModel
	err := m.collection.FindOne(ctx, filter).Decode(&orderModel)
	if err != nil {
		if err == mongo.ErrNoDocuments {
			return nil, nil
		}
		return nil, err
	}

	order, err := orderModel.ToDomain()
	if err != nil {
		return nil, err
	}

	return order, nil
}

// FindAll retrieves all orders from the MongoDB collection.
func (m *mongoOrderRepository) FindAll(ctx context.Context) ([]*domain.Order, error) {
	cursor, err := m.collection.Find(ctx, bson.M{})
	if err != nil {
		return nil, err
	}
	defer cursor.Close(ctx)

	var orders []*domain.Order
	for cursor.Next(ctx) {
		var orderModel models.OrderModel
		if err := cursor.Decode(&orderModel); err != nil {
			return nil, err
		}

		order, err := orderModel.ToDomain()
		if err != nil {
			return nil, err
		}
		orders = append(orders, order)
	}
	if err := cursor.Err(); err != nil {
		return nil, err
	}

	return orders, nil
}

// FindByCustomerId retrieves all orders associated with a specific customer.
func (m *mongoOrderRepository) FindByCustomerId(ctx context.Context, customerId string) ([]*domain.Order, error) {
	filter := bson.M{"customerId": customerId}
	cursor, err := m.collection.Find(ctx, filter)
	if err != nil {
		return nil, err
	}
	defer cursor.Close(ctx)

	var orders []*domain.Order
	for cursor.Next(ctx) {
		var orderModel models.OrderModel
		if err := cursor.Decode(&orderModel); err != nil {
			return nil, err
		}

		order, err := orderModel.ToDomain()
		if err != nil {
			return nil, err
		}
		orders = append(orders, order)
	}
	if err := cursor.Err(); err != nil {
		return nil, err
	}

	return orders, nil
}

// FindByStatus retrieves all orders with a specific status.
func (m *mongoOrderRepository) FindByStatus(ctx context.Context, status string) ([]*domain.Order, error) {
	filter := bson.M{"status": status}
	cursor, err := m.collection.Find(ctx, filter)
	if err != nil {
		return nil, err
	}
	defer cursor.Close(ctx)

	var orders []*domain.Order
	for cursor.Next(ctx) {
		var orderModel models.OrderModel
		if err := cursor.Decode(&orderModel); err != nil {
			return nil, err
		}

		order, err := orderModel.ToDomain()
		if err != nil {
			return nil, err
		}
		orders = append(orders, order)
	}
	if err := cursor.Err(); err != nil {
		return nil, err
	}

	return orders, nil
}

// Save persists the given order into the MongoDB collection.
func (m *mongoOrderRepository) Save(ctx context.Context, order *domain.Order) error {
	orderModel, err := models.OrderModelFromDomain(order)
	if err != nil {
		return err
	}

	_, err = m.collection.InsertOne(ctx, orderModel)
	if err != nil {
		return err
	}

	return nil
}

// UpsertByOrderId updates an order in the MongoDB collection if it exists,
// or inserts it if it doesn't exist, based on the order ID.
func (m *mongoOrderRepository) UpsertByOrderId(ctx context.Context, order *domain.Order) error {
	orderModel, err := models.OrderModelFromDomain(order)
	if err != nil {
		return err
	}
	orderModel.UpdatedAt = time.Now()

	filter := bson.M{"orderId": order.OrderId}
	update := bson.M{"$set": orderModel}

	_, err = m.collection.UpdateOne(ctx, filter, update, options.Update().SetUpsert(true))
	if err != nil {
		return err
	}

	return nil
}
