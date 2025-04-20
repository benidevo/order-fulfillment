package eventhandlers

import "context"

// EventHandler interface defines the contract for all event handlers
type EventHandler interface {
	HandleEvent(ctx context.Context, event interface{}) error
}
