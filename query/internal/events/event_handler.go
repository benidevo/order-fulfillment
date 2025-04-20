package events

import (
	"encoding/json"
	"time"
)

// BaseEvent represents the core structure for all events within the system.
// It encapsulates common metadata and payload information needed for event processing.
type BaseEvent struct {
	EventID       string          `json:"eventId"`
	EventType     string          `json:"eventType"`
	AggregateID   string          `json:"aggregateId"`
	AggregateType string          `json:"aggregateType"`
	Timestamp     time.Time       `json:"timestamp"`
	Version       int64           `json:"version"`
	Payload       json.RawMessage `json:"payload"`
}
