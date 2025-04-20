package events

import (
	"encoding/json"
	"time"
)

// CustomTime is a wrapper around time.Time that implements custom JSON unmarshaling
type CustomTime struct {
	time.Time
}

// UnmarshalJSON implements custom unmarshaling for timestamps with flexible format handling
func (ct *CustomTime) UnmarshalJSON(data []byte) error {
	var raw string
	if err := json.Unmarshal(data, &raw); err != nil {
		return err
	}

	t, err := time.Parse(time.RFC3339Nano, raw)
	if err != nil {
		t, err = time.Parse("2006-01-02T15:04:05.999999999", raw)
		if err != nil {
			return err
		}
	}

	*ct = CustomTime{t}
	return nil
}

// BaseEvent represents the core structure for all events within the system.
// It encapsulates common metadata and payload information needed for event processing.
type BaseEvent struct {
	EventID       string          `json:"eventId"`
	EventType     string          `json:"eventType"`
	AggregateID   string          `json:"aggregateId"`
	AggregateType string          `json:"aggregateType"`
	Timestamp     CustomTime      `json:"timestamp"`
	Version       int64           `json:"version"`
	Payload       json.RawMessage `json:"payload"`
}
