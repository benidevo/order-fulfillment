package handlers

import (
	"fmt"
	"net/http"

	"github.com/benidevo/order-fulfillment/query/internal/api/dtos/response"
	"github.com/gin-gonic/gin"
)

// OrdersHandler handles HTTP requests related to orders.
// It provides methods for retrieving orders.
type OrdersHandler struct {
}

// NewOrdersHandler creates and returns a new instance of OrdersHandler.
// This handler is responsible for managing HTTP requests related to orders.
func NewOrdersHandler() *OrdersHandler {
	return &OrdersHandler{}
}

// ListOrders handles the HTTP GET request to list orders.
// It responds with a JSON object containing a success message.
func (handler *OrdersHandler) ListOrders(c *gin.Context) {
	c.JSON(http.StatusOK, response.NewResponse("Welcome to Orders routes"))
}

// GetOrder handles the HTTP GET request to retrieve a specific order by its ID.
func (handler *OrdersHandler) GetOrder(c *gin.Context) {
	orderId := c.Param("id")
	result := fmt.Sprintf("Here is the order id: %s", orderId)
	c.JSON(http.StatusOK, response.NewResponse(result))
}
