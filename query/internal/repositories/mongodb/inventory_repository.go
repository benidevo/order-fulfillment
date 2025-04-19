package mongodb

import (
	"context"
	"errors"
	"time"

	"github.com/benidevo/order-fulfillment/query/internal/domain"
	"github.com/benidevo/order-fulfillment/query/internal/models"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

var ErrInventoryNotFound = errors.New("inventory item not found")

type mongoInventoryRepository struct {
	collection *mongo.Collection
}

// Creates a new instance of mongoInventoryRepository
// with a reference to the specified MongoDB collection based on the InventoryCollectionName.
//
// Parameters:
//   - db: A pointer to the mongo.Database where the inventory collection resides
//
// Returns:
//   - *mongoInventoryRepository: A pointer to the newly created repository instance
func NewMongoInventoryRepository(db *mongo.Database) *mongoInventoryRepository {
	return &mongoInventoryRepository{
		collection: db.Collection(models.InventoryCollectionName),
	}
}

// FindById retrieves an inventory item by its ID from the MongoDB collection.
//
// Parameters:
//   - ctx: Context for the database operation
//   - id: String representation of the MongoDB ObjectID of the inventory item
//
// Returns:
//   - *domain.InventoryItem: The found inventory item, or nil if not found
//   - error: An error if the ID is invalid, if there's a database error, or if there's an error during domain conversion
func (m *mongoInventoryRepository) FindById(ctx context.Context, id string) (*domain.InventoryItem, error) {
	var err error
	objectID, err := primitive.ObjectIDFromHex(id)
	if err != nil {
		return nil, err
	}
	filter := bson.M{"_id": objectID}

	var inventoryModel models.InventoryModel

	err = m.collection.FindOne(ctx, filter).Decode(&inventoryModel)
	if err != nil {
		if err == mongo.ErrNoDocuments {
			return nil, nil
		}
		return nil, err
	}

	inventoryItem, err := inventoryModel.ToDomain()
	if err != nil {
		return nil, err
	}

	return inventoryItem, nil
}

// FindByProductId retrieves an inventory item from MongoDB by its product ID.
//
// Parameters:
//   - ctx: Context for the database operation
//   - productId: Unique identifier of the product to find
//
// Returns:
//   - *domain.InventoryItem: The inventory item if found
//   - error: ErrInventoryNotFound if not found, or any other database error
func (m *mongoInventoryRepository) FindByProductId(ctx context.Context, productId string) (*domain.InventoryItem, error) {
	filter := bson.M{"productId": productId}
	var inventoryModel models.InventoryModel

	err := m.collection.FindOne(ctx, filter).Decode(&inventoryModel)
	if err != nil {
		if err == mongo.ErrNoDocuments {
			return nil, ErrInventoryNotFound
		}
		return nil, err
	}

	inventoryItem, err := inventoryModel.ToDomain()
	if err != nil {
		return nil, err
	}

	return inventoryItem, nil
}

// FindAll retrieves all inventory items from the MongoDB collection.
//
// Parameters:
//   - ctx: A context.Context for controlling the request's deadline, cancellation, etc.
//
// Returns:
//   - []*domain.InventoryItem: A slice of inventory item domain objects if successful
//   - error: An error if the database operation fails, cursor iteration fails, or document
//     decoding/conversion encounters an issue
func (m *mongoInventoryRepository) FindAll(ctx context.Context) ([]*domain.InventoryItem, error) {
	cursor, err := m.collection.Find(ctx, bson.M{})
	if err != nil {
		return nil, err
	}
	defer cursor.Close(ctx)

	var inventoryItems []*domain.InventoryItem
	for cursor.Next(ctx) {
		var inventoryModel models.InventoryModel
		if err := cursor.Decode(&inventoryModel); err != nil {
			return nil, err
		}

		inventoryItem, err := inventoryModel.ToDomain()
		if err != nil {
			return nil, err
		}
		inventoryItems = append(inventoryItems, inventoryItem)
	}
	if err := cursor.Err(); err != nil {
		return nil, err
	}

	return inventoryItems, nil
}

// Save persists the given inventory item into the MongoDB collection.
//
// Parameters:
//   - ctx: The context for the database operation
//   - item: The domain model inventory item to be saved
//
// Returns:
//   - error: nil if successful, otherwise an error describing what went wrong
func (m *mongoInventoryRepository) Save(ctx context.Context, item *domain.InventoryItem) error {
	inventoryModel, err := models.InventoryModelFromDomain(item)
	if err != nil {
		return err
	}

	_, err = m.collection.InsertOne(ctx, inventoryModel)
	if err != nil {
		return err
	}

	return nil
}

// UpsertByProductId updates an inventory item in the MongoDB collection if it exists,
// or inserts it if it doesn't exist, based on the product ID.
//
// Parameters:
//   - ctx: The context for the database operation
//   - item: The inventory item to upsert
//
// Returns:
//   - error: nil if the operation is successful, otherwise the error that occurred
func (m *mongoInventoryRepository) UpsertByProductId(ctx context.Context, item *domain.InventoryItem) error {
	inventoryModel, err := models.InventoryModelFromDomain(item)
	if err != nil {
		return err
	}
	inventoryModel.UpdatedAt = time.Now()

	filter := bson.M{"productId": item.ProductId}
	update := bson.M{"$set": inventoryModel}

	_, err = m.collection.UpdateOne(ctx, filter, update, options.Update().SetUpsert(true))
	if err != nil {
		return err
	}

	return nil
}
