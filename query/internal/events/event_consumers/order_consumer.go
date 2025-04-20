package consumers

import (
	"context"
	"encoding/json"
	"errors"

	"github.com/IBM/sarama"
	"github.com/benidevo/order-fulfillment/query/internal/config"
	"github.com/benidevo/order-fulfillment/query/internal/events"
	"github.com/benidevo/order-fulfillment/query/internal/events/event_handlers"
)

// Constants for order event types
const (
	OrderCreatedEvent       = "OrderCreated"
	OrderStatusUpdatedEvent = "OrderStatusUpdated"
	OrderCanceledEvent      = "OrderCanceled"
)

// OrderConsumer handles consuming order events from Kafka
type OrderConsumer struct {
	baseConsumer *BaseConsumer
	handler      *eventhandlers.OrderEventHandler
}

// NewOrderConsumer creates a new consumer for order events
func NewOrderConsumer(cfg *config.Config, handler *eventhandlers.OrderEventHandler) (*OrderConsumer, error) {
	messageHandler := func(ctx context.Context, message *sarama.ConsumerMessage) error {
		return processOrderMessage(ctx, message, handler)
	}

	baseConsumer, err := NewBaseConsumer(ConsumerConfig{
		Brokers: cfg.KafkaBrokers,
		Topic:   "order-events",
		GroupID: "order-consumer-group",
		Handler: messageHandler,
	})

	if err != nil {
		return nil, err
	}

	return &OrderConsumer{
		baseConsumer: baseConsumer,
		handler:      handler,
	}, nil
}

// Start begins consuming order events
func (c *OrderConsumer) Start(ctx context.Context) error {
	return c.baseConsumer.Start(ctx)
}

func processOrderMessage(ctx context.Context, message *sarama.ConsumerMessage, handler *eventhandlers.OrderEventHandler) error {
	var eventMessage events.BaseEvent
	if err := json.Unmarshal(message.Value, &eventMessage); err != nil {
		return err
	}

	switch eventMessage.EventType {
	case OrderCreatedEvent:
		var payload events.OrderCreatedPayload
		if err := json.Unmarshal(eventMessage.Payload, &payload); err != nil {
			return err
		}

		event := &events.OrderCreatedEvent{
			BaseEvent: eventMessage,
			Payload:   payload,
		}

		return handler.HandleOrderCreated(ctx, event)

	case OrderStatusUpdatedEvent:
		var payload events.OrderStatusUpdatedPayload
		if err := json.Unmarshal(eventMessage.Payload, &payload); err != nil {
			return err
		}

		event := &events.OrderStatusUpdatedEvent{
			BaseEvent: eventMessage,
			Payload:   payload,
		}

		return handler.HandleOrderStatusUpdated(ctx, event)

	case OrderCanceledEvent:
		var payload events.OrderCancelledPayload
		if err := json.Unmarshal(eventMessage.Payload, &payload); err != nil {
			return err
		}

		event := &events.OrderCancelledEvent{
			BaseEvent: eventMessage,
			Payload:   payload,
		}

		return handler.HandleOrderCancelled(ctx, event)

	default:
		return errors.New("unknown order event type: " + eventMessage.EventType)
	}
}
