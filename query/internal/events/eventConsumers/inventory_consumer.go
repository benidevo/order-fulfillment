package eventConsumers

import (
	"context"
	"encoding/json"
	"errors"

	"github.com/IBM/sarama"
	"github.com/benidevo/order-fulfillment/query/internal/config"
	"github.com/benidevo/order-fulfillment/query/internal/events"
	"github.com/benidevo/order-fulfillment/query/internal/events/eventHandlers"
)

// Constants for inventory event types
const (
	InventoryUpdatedEvent   = "InventoryUpdated"
	InventoryAllocatedEvent = "InventoryAllocated"
	InventoryReturnedEvent  = "InventoryReturned"
)

// InventoryConsumer handles consuming inventory events from Kafka
type InventoryConsumer struct {
	baseConsumer *BaseConsumer
	handler      *eventHandlers.InventoryEventHandler
}

// NewInventoryConsumer creates and configures a new consumer for inventory events.
// It initializes a base Kafka consumer with the specified configuration and sets up
// a message handler that processes inventory events using the provided handler.
func NewInventoryConsumer(cfg *config.Config, handler *eventHandlers.InventoryEventHandler) (*InventoryConsumer, error) {
	messageHandler := func(ctx context.Context, message *sarama.ConsumerMessage) error {
		return processInventoryMessage(ctx, message, handler)
	}

	baseConsumer, err := NewBaseConsumer(ConsumerConfig{
		Brokers: cfg.KafkaBrokers,
		Topic:   "inventory-events",
		GroupID: "inventory-consumer-group",
		Handler: messageHandler,
	})

	if err != nil {
		return nil, err
	}

	return &InventoryConsumer{
		baseConsumer: baseConsumer,
		handler:      handler,
	}, nil
}

// Start begins consuming inventory events
func (c *InventoryConsumer) Start(ctx context.Context) error {
	return c.baseConsumer.Start(ctx)
}

func processInventoryMessage(ctx context.Context, message *sarama.ConsumerMessage, handler *eventHandlers.InventoryEventHandler) error {
	var eventMessage events.BaseEvent
	if err := json.Unmarshal(message.Value, &eventMessage); err != nil {
		return err
	}

	switch eventMessage.EventType {
	case InventoryUpdatedEvent:
		var payload events.InventoryUpdatedPayload
		if err := json.Unmarshal(eventMessage.Payload, &payload); err != nil {
			return err
		}

		event := &events.InventoryUpdatedEvent{
			BaseEvent: eventMessage,
			Payload:   payload,
		}

		return handler.HandleInventoryUpdated(ctx, event)

	case InventoryAllocatedEvent:
		var payload events.InventoryAllocatedPayload
		if err := json.Unmarshal(eventMessage.Payload, &payload); err != nil {
			return err
		}

		event := &events.InventoryAllocatedEvent{
			BaseEvent: eventMessage,
			Payload:   payload,
		}

		return handler.HandleInventoryAllocated(ctx, event)

	case InventoryReturnedEvent:
		var payload events.InventoryReturnedPayload
		if err := json.Unmarshal(eventMessage.Payload, &payload); err != nil {
			return err
		}

		event := &events.InventoryReturnedEvent{
			BaseEvent: eventMessage,
			Payload:   payload,
		}

		return handler.HandleInventoryReturned(ctx, event)

	default:
		return errors.New("unknown inventory event type: " + eventMessage.EventType)
	}
}
