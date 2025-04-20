package eventhandlers

import (
	"context"
	"time"

	"github.com/benidevo/order-fulfillment/query/internal/domain"
	"github.com/benidevo/order-fulfillment/query/internal/events"
	"github.com/benidevo/order-fulfillment/query/internal/repositories"
	"github.com/shopspring/decimal"
)

// OrderEventHandler handles all order-related events
type OrderEventHandler struct {
	orderRepository repositories.OrderRepository
}

// NewOrderEventHandler creates a new OrderEventHandler
func NewOrderEventHandler(orderRepo repositories.OrderRepository) *OrderEventHandler {
	return &OrderEventHandler{
		orderRepository: orderRepo,
	}
}

// HandleOrderCreated processes an OrderCreatedEvent by transforming event data into a domain Order object
// and persisting it to the repository.
func (handler *OrderEventHandler) HandleOrderCreated(ctx context.Context, event *events.OrderCreatedEvent) error {
	issuedAt, err := time.Parse(time.RFC3339, event.Payload.IssuedAt)
	if err != nil {
		issuedAt = time.Now()
	}

	items := make([]domain.OrderItem, 0, len(event.Payload.Items))
	for _, item := range event.Payload.Items {
		value, _ := decimal.NewFromString(item.Price.Value.String())

		orderItem := domain.OrderItem{
			ProductId: item.ProductId,
			Quantity:  item.Quantity,
			Price: domain.Money{
				Currency: item.Price.Currency,
				Value:    value,
			},
		}
		items = append(items, orderItem)
	}

	shippingAddress := domain.Address{
		Street:  event.Payload.ShippingAddress.Street,
		City:    event.Payload.ShippingAddress.City,
		State:   event.Payload.ShippingAddress.State,
		ZipCode: event.Payload.ShippingAddress.ZipCode,
		Country: event.Payload.ShippingAddress.Country,
	}

	billingAddress := domain.Address{
		Street:  event.Payload.BillingAddress.Street,
		City:    event.Payload.BillingAddress.City,
		State:   event.Payload.BillingAddress.State,
		ZipCode: event.Payload.BillingAddress.ZipCode,
		Country: event.Payload.BillingAddress.Country,
	}

	totalValue, _ := decimal.NewFromString(event.Payload.TotalCost.Value.String())

	status, err := domain.OrderStatusFromString(event.Payload.Status)
	if err != nil {
		status = domain.OrderStatusRegistered
	}

	order := &domain.Order{
		OrderId:         event.Payload.OrderId,
		CustomerId:      event.Payload.CustomerId,
		Items:           items,
		Status:          status,
		ShippingAddress: shippingAddress,
		BillingAddress:  billingAddress,
		TotalCost: domain.Money{
			Currency: event.Payload.TotalCost.Currency,
			Value:    totalValue,
		},
		IssuedAt:  issuedAt,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	return handler.orderRepository.UpsertByOrderId(ctx, order)
}

// HandleOrderStatusUpdated processes an OrderStatusUpdatedEvent
func (handler *OrderEventHandler) HandleOrderStatusUpdated(ctx context.Context, event *events.OrderStatusUpdatedEvent) error {
	order, err := handler.orderRepository.FindById(ctx, event.Payload.OrderId)
	if err != nil {
		return err
	}

	if order == nil {
		return nil
	}

	status, err := domain.OrderStatusFromString(event.Payload.Status)
	if err != nil {
		return err
	}

	order.Status = status
	order.UpdatedAt = time.Now()

	return handler.orderRepository.UpsertByOrderId(ctx, order)
}

// HandleOrderCancelled processes an OrderCancelledEvent
func (handler *OrderEventHandler) HandleOrderCancelled(ctx context.Context, event *events.OrderCancelledEvent) error {
	order, err := handler.orderRepository.FindById(ctx, event.Payload.OrderId)
	if err != nil {
		return err
	}

	if order == nil {
		return nil
	}
	order.Status = domain.OrderStatusCancelled
	order.UpdatedAt = time.Now()

	return handler.orderRepository.UpsertByOrderId(ctx, order)
}
