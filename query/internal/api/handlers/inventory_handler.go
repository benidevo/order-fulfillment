package handlers

import (
	"net/http"

	"github.com/benidevo/order-fulfillment/query/internal/api/dtos/response"
	"github.com/gin-gonic/gin"
)

// InventoryHandler handles HTTP requests related to inventory operations.
// It provides methods for querying data.
type InventoryHandler struct {
}

// NewInventoryHandler creates and returns a new instance of InventoryHandler.
// It initializes a new handler without any dependencies.
// This handler is responsible for processing inventory-related operations.
func NewInventoryHandler() *InventoryHandler {
	return &InventoryHandler{}
}

// ListInventory handles the HTTP GET request to list inventory items.
// It responds with a JSON object containing a success message.
func (handler *InventoryHandler) ListInventory(c *gin.Context) {

	c.JSON(http.StatusOK, response.NewResponse("Welcome to Inventory Handler"))
}
