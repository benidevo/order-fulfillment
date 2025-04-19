package eventConsumers

import (
	"context"
	"log"
	"os"
	"os/signal"
	"syscall"

	"github.com/IBM/sarama"
)

// BaseConsumer provides common functionality for all Kafka consumers
type BaseConsumer struct {
	consumer       sarama.ConsumerGroup
	topic          string
	groupID        string
	ready          chan bool
	messageHandler func(context.Context, *sarama.ConsumerMessage) error
}

// ConsumerConfig holds configuration for creating a consumer
type ConsumerConfig struct {
	Brokers string
	Topic   string
	GroupID string
	Handler func(context.Context, *sarama.ConsumerMessage) error
}

// NewBaseConsumer creates a new base Kafka consumer
func NewBaseConsumer(cfg ConsumerConfig) (*BaseConsumer, error) {
	kafkaConfig := sarama.NewConfig()
	kafkaConfig.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategyRoundRobin()
	kafkaConfig.Consumer.Offsets.Initial = sarama.OffsetOldest

	brokers := []string{cfg.Brokers}
	group, err := sarama.NewConsumerGroup(brokers, cfg.GroupID, kafkaConfig)
	if err != nil {
		return nil, err
	}

	return &BaseConsumer{
		consumer:       group,
		topic:          cfg.Topic,
		groupID:        cfg.GroupID,
		ready:          make(chan bool),
		messageHandler: cfg.Handler,
	}, nil
}

// Start initiates Kafka message consumption for the consumer's configured topic.
// It handles graceful shutdown on receiving termination signals (SIGINT, SIGTERM) or when
// the provided context is cancelled.
func (c *BaseConsumer) Start(ctx context.Context) error {

	consumerCtx, cancel := context.WithCancel(ctx)
	defer cancel()

	signals := make(chan os.Signal, 1)
	signal.Notify(signals, syscall.SIGINT, syscall.SIGTERM)

	topics := []string{c.topic}
	go func() {
		for {
			if err := c.consumer.Consume(consumerCtx, topics, c); err != nil {
				log.Printf("Error from consumer for topic %s: %v", c.topic, err)
			}

			if consumerCtx.Err() != nil {
				return
			}

			c.ready = make(chan bool)
		}
	}()

	<-c.ready
	log.Printf("Kafka consumer for topic %s started", c.topic)

	select {
	case <-signals:
		log.Printf("Received termination signal, shutting down consumer for topic %s...", c.topic)
		cancel()
	case <-ctx.Done():
		log.Printf("Context cancelled, shutting down consumer for topic %s...", c.topic)
	}

	return c.consumer.Close()
}

// Setup is run at the beginning of a new session
func (c *BaseConsumer) Setup(sarama.ConsumerGroupSession) error {
	close(c.ready)
	return nil
}

// Cleanup is run at the end of a session
func (c *BaseConsumer) Cleanup(sarama.ConsumerGroupSession) error {
	return nil
}

// ConsumeClaim implements the sarama.ConsumerGroupHandler interface.
// It processes messages from a claimed partition.
//
// If message processing fails, the error is logged but the method continues to the next message.
// This allows the consumer group to make progress even when individual messages fail.
func (c *BaseConsumer) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for message := range claim.Messages() {
		log.Printf("Received message: topic=%s, partition=%d, offset=%d, key=%s",
			message.Topic, message.Partition, message.Offset, string(message.Key))

		if err := c.messageHandler(session.Context(), message); err != nil {
			log.Printf("Error processing message from topic %s: %v", c.topic, err)
		}

		session.MarkMessage(message, "")
	}

	return nil
}
