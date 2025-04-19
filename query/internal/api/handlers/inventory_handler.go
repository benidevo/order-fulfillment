package handlers

import (
	"net/http"

	"github.com/benidevo/order-fulfillment/query/internal/api/dtos/response"
	"github.com/benidevo/order-fulfillment/query/internal/repositories"
	"github.com/gin-gonic/gin"
)

// InventoryHandler handles HTTP requests related to inventory operations.
// It provides methods for querying data.
type InventoryHandler struct {
	inventoryRepository repositories.InventoryRepository
}

// NewInventoryHandler creates and returns a new instance of InventoryHandler.
// It initializes a new handler with the provided inventory repository.
// This handler is responsible for processing inventory-related operations.
func NewInventoryHandler(inventoryRepository repositories.InventoryRepository) *InventoryHandler {
	return &InventoryHandler{
		inventoryRepository: inventoryRepository,
	}
}

// ListInventory handles HTTP requests to retrieve all inventory items.
// It fetches all inventory items from the repository and returns them in the response.
// If the retrieval fails or no items are found, it returns a NOT_FOUND error.
func (handler *InventoryHandler) ListInventory(c *gin.Context) {
	inventoryItems, err := handler.inventoryRepository.FindAll(c)
	if err != nil || len(inventoryItems) == 0 {
		c.JSON(http.StatusNotFound, response.NewErrorResponse("NOT_FOUND", "Failed to retrieve inventory items", nil))
		return
	}

	c.JSON(http.StatusOK, response.NewResponse(inventoryItems))
}
