package handlers

import (
	"log"
	"net/http"

	"github.com/benidevo/order-fulfillment/query/internal/api/dtos/response"

	"github.com/benidevo/order-fulfillment/query/internal/repositories"
	"github.com/gin-gonic/gin"
)

// OrdersHandler handles HTTP requests related to orders.
// It provides methods for retrieving orders.
type OrdersHandler struct {
	orderRepository repositories.OrderRepository
}

// NewOrdersHandler creates and returns a new instance of OrdersHandler.
// This handler is responsible for managing HTTP requests related to orders.
func NewOrdersHandler(orderRepository repositories.OrderRepository) *OrdersHandler {
	return &OrdersHandler{
		orderRepository,
	}
}

// ListOrders handles the HTTP request to fetch all orders.
// It returns a list of orders in the response body with HTTP status 200 OK.
// If there's an error during the fetching process, it returns an HTTP status 500 Internal Server Error.
func (handler *OrdersHandler) ListOrders(c *gin.Context) {
	orders, err := handler.orderRepository.FindAll(c)
	if err != nil {
		log.Println("Unexpected error while fetching orders; %w", err)
		c.JSON(http.StatusInternalServerError, response.NewErrorResponse("SOMETHING_WENT_WRONG", "", nil))
		return
	}

	c.JSON(http.StatusOK, response.NewResponse(orders))
}

// GetOrder handles the HTTP GET request to retrieve a specific order by its ID.
func (handler *OrdersHandler) GetOrder(c *gin.Context) {
	orderId := c.Param("id")
	order, err := handler.orderRepository.FindById(c, orderId)
	if err != nil || order == nil {
		log.Println("No order found: %w", err)
		c.JSON(http.StatusNoContent, nil)
		return
	}

	c.JSON(http.StatusOK, response.NewResponse(order))
}
