package models

import (
	"fmt"
	"time"

	"github.com/benidevo/order-fulfillment/query/internal/domain"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

const (
	InventoryCollectionName = "inventory"
)

// Represents the inventory state of a product in the system.
//
// It tracks both available and allocated quantities as well as the overall status.
// The model is mapped to MongoDB using BSON tags.
type InventoryModel struct {
	Id                primitive.ObjectID `bson:"_id"`
	ProductId         string             `bson:"productId"`
	AvailableQuantity int                `bson:"availableQuantity"`
	AllocatedQuantity int                `bson:"allocatedQuantity"`
	Status            string             `bson:"status"`
	CreatedAt         time.Time          `bson:"createdAt"`
	UpdatedAt         time.Time          `bson:"updatedAt"`
}

// InventoryIndexes defines the MongoDB indexes for the "inventory" collection.
// It includes a unique index on the "productId" field to ensure each product
// has only one inventory record in the collection.
var InventoryIndexes = []mongo.IndexModel{
	{
		Keys:    bson.D{{Key: "productId", Value: 1}},
		Options: options.Index().SetUnique(true),
	},
}

// Converts the inventory model to a domain inventory item.
//
// Returns a pointer to a domain.InventoryItem and nil error on success.
// Returns nil and an error if the inventory status is invalid.
func (i *InventoryModel) ToDomain() (*domain.InventoryItem, error) {
	status, err := domain.InventoryStatusFromString(i.Status)
	if err != nil {
		return nil, fmt.Errorf("invalid inventory status: %w", err)
	}

	return &domain.InventoryItem{
		Id:                i.Id.Hex(),
		ProductId:         i.ProductId,
		AvailableQuantity: i.AvailableQuantity,
		AllocatedQuantity: i.AllocatedQuantity,
		Status:            status,
		CreatedAt:         i.CreatedAt,
		UpdatedAt:         i.UpdatedAt,
	}, nil

}

// Converts a domain InventoryItem to a database InventoryModel.
//
// It handles the conversion of string ID to MongoDB ObjectID, generating a new ObjectID
// if the domain ID is empty. The function maps all other fields from the domain model
// to their corresponding database model fields.
//
// Parameters:
//   - i: A pointer to the domain.InventoryItem to be converted
//
// Returns:
//   - *InventoryModel: A pointer to the converted InventoryModel
//   - error: An error if the ID conversion fails or nil if successful
func InventoryModelFromDomain(i *domain.InventoryItem) (*InventoryModel, error) {
	var id primitive.ObjectID
	if i.Id != "" {
		var err error
		id, err = primitive.ObjectIDFromHex(i.Id)
		if err != nil {
			return nil, fmt.Errorf("invalid inventory ID: %w", err)
		}

	} else {
		id = primitive.NewObjectID()
	}

	return &InventoryModel{
		Id:                id,
		ProductId:         i.ProductId,
		AvailableQuantity: i.AvailableQuantity,
		AllocatedQuantity: i.AllocatedQuantity,
		Status:            i.Status.String(),
		CreatedAt:         i.CreatedAt,
		UpdatedAt:         i.UpdatedAt,
	}, nil
}
