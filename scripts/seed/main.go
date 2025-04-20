package main

import (
	"log"
	"math/rand"
	"time"
)

func main() {
	log.Println("Starting Order Fulfillment data seeding...")

	cfg := LoadConfig()

	r := rand.New(rand.NewSource(cfg.RandomSeed))

	log.Printf("Generating %d products...", cfg.NumProducts)
	products := GenerateProducts(cfg.NumProducts, r)

	if err := SeedInventory(cfg.BaseURL, products); err != nil {
		log.Fatalf("Failed to seed inventory: %v", err)
	}

	numCustomers := cfg.NumOrders / 3
	if numCustomers < 2 {
		numCustomers = 2
	}
	log.Printf("Generating %d customers...", numCustomers)
	customers := GenerateCustomers(numCustomers, r)

	log.Printf("Generating %d orders...", cfg.NumOrders)
	orders := GenerateOrders(cfg.NumOrders, customers, products, r)

	createdOrders, err := SeedOrders(cfg.BaseURL, orders)
	if err != nil {
		log.Fatalf("Failed to seed orders: %v", err)
	}

	if err := UpdateOrderStatuses(cfg.BaseURL, createdOrders, r, cfg.EnableCancel, cfg.EnableShip, cfg.EnableDeliver); err != nil {
		log.Printf("Warning: Failed to update some order statuses: %v", err)
	}

	log.Println("Waiting for events to propagate to query service...")
	time.Sleep(2 * time.Second)

	log.Println("Data seeding completed successfully!")
	log.Printf("Created %d products, %d customers, and %d orders", len(products), len(customers), len(createdOrders))
	log.Println("You can now access the system at:", cfg.BaseURL)
}
