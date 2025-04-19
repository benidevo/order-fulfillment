package main

import (
	"log"

	"github.com/benidevo/order-fulfillment/query/internal/app"
	"github.com/benidevo/order-fulfillment/query/internal/config"
)

func main() {
	var err error
	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	application, err := app.New(cfg)
	if err != nil {
		log.Fatalf("Failed to initialize application: %v", err)
	}

	err = application.Run()
	if err != nil {
		log.Fatalf("Failed to start application: %v", err)
	}
}
