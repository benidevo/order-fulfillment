package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"log"
	"math/big"
	"math/rand"
	"net/http"
	"time"
)

// GenerateCustomers generates a list of customers with addresses
func GenerateCustomers(numCustomers int, r *rand.Rand) []Customer {
	customers := make([]Customer, numCustomers)

	firstNames := []string{"John", "Jane", "Alice", "Bob", "Maria", "Michael", "Emily", "David", "Sarah", "James"}
	lastNames := []string{"Smith", "Johnson", "Brown", "Davis", "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Jackson"}

	cities := []string{"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"}
	states := []string{"NY", "CA", "IL", "TX", "AZ", "PA", "TX", "CA", "TX", "CA"}

	for i := 0; i < numCustomers; i++ {
		firstName := firstNames[r.Intn(len(firstNames))]
		lastName := lastNames[r.Intn(len(lastNames))]
		cityIndex := r.Intn(len(cities))

		customers[i] = Customer{
			ID:   fmt.Sprintf("cust-%03d", i+1),
			Name: fmt.Sprintf("%s %s", firstName, lastName),
			Address: AddressDto{
				Street:  fmt.Sprintf("%d %s St", 100+r.Intn(900), generateStreetName(r)),
				City:    cities[cityIndex],
				State:   states[cityIndex],
				Zipcode: fmt.Sprintf("%05d", 10000+r.Intn(90000)),
				Country: "USA",
			},
		}
	}

	return customers
}

// GenerateOrders generates a list of orders based on customers and products
func GenerateOrders(numOrders int, customers []Customer, products []Product, r *rand.Rand) []Order {
	orders := make([]Order, 0, numOrders)

	now := time.Now()
	startDate := now.AddDate(0, 0, -30)

	for i := 0; i < numOrders; i++ {
		customer := customers[r.Intn(len(customers))]

		numItems := 1 + r.Intn(5)
		items := make([]OrderItemDto, 0, numItems)

		shuffledProductIndices := r.Perm(len(products))

		totalCost := big.NewFloat(0)

		for j := 0; j < numItems; j++ {
			productIndex := shuffledProductIndices[j]
			product := products[productIndex]

			quantity := 1 + r.Intn(3)

			item := OrderItemDto{
				ProductId: product.ID,
				Quantity:  quantity,
				Price:     product.Price,
			}
			items = append(items, item)

			itemCost := new(big.Float).Mul(product.Price, big.NewFloat(float64(quantity)))
			totalCost = new(big.Float).Add(totalCost, itemCost)
		}

		duration := now.Sub(startDate)
		randomDuration := time.Duration(r.Int63n(int64(duration)))
		issuedAt := startDate.Add(randomDuration)

		shippingAddress := customer.Address
		billingAddress := customer.Address

		order := Order{
			ID:              fmt.Sprintf("order-%03d", i+1),
			CustomerID:      customer.ID,
			Items:           items,
			Status:          "REGISTERED",
			ShippingAddress: shippingAddress,
			BillingAddress:  billingAddress,
			TotalCost:       totalCost,
			IssuedAt:        issuedAt,
		}

		orders = append(orders, order)
	}

	return orders
}

func SeedOrders(baseURL string, orders []Order) ([]Order, error) {
	client := &http.Client{}

	log.Println("Seeding orders...")

	createdOrders := make([]Order, 0, len(orders))

	for _, order := range orders {
		orderDto := OrderCreateDto{
			CustomerId:      order.CustomerID,
			Items:           order.Items,
			ShippingAddress: order.ShippingAddress,
			BillingAddress:  order.BillingAddress,
			TotalCost:       order.TotalCost,
			IssuedAt:        order.IssuedAt,
		}

		jsonData, err := json.Marshal(orderDto)
		if err != nil {
			return nil, fmt.Errorf("failed to marshal order create dto: %w", err)
		}

		url := fmt.Sprintf("%s/api/v1/orders", baseURL)
		req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(jsonData))
		if err != nil {
			return nil, fmt.Errorf("failed to create order create request: %w", err)
		}

		req.Header.Set("Content-Type", "application/json")

		resp, err := client.Do(req)
		if err != nil {
			return nil, fmt.Errorf("failed to create order: %w", err)
		}

		var responseDto ResponseDto
		err = json.NewDecoder(resp.Body).Decode(&responseDto)
		resp.Body.Close()

		if err != nil {
			return nil, fmt.Errorf("failed to decode response for order creation: %w", err)
		}

		if !responseDto.Success {
			return nil, fmt.Errorf("failed to create order: %v", responseDto)
		}

		log.Printf("Created order %s for customer %s with %d items", order.ID, order.CustomerID, len(order.Items))
		createdOrders = append(createdOrders, order)
	}

	log.Printf("Successfully seeded %d orders", len(createdOrders))
	return createdOrders, nil
}

// UpdateOrderStatuses updates the statuses of some orders to create a realistic distribution
func UpdateOrderStatuses(baseURL string, orders []Order, r *rand.Rand, enableCancel, enableShip, enableDeliver bool) error {
	client := &http.Client{}

	log.Println("Updating order statuses...")

	// Define the percentage of orders in each state
	cancelledPercentage := 0.0
	shippedPercentage := 0.0
	deliveredPercentage := 0.0

	if enableCancel {
		cancelledPercentage = 0.15 // 15% cancelled
	}

	if enableShip {
		shippedPercentage = 0.30 // 30% shipped
	}

	if enableDeliver {
		deliveredPercentage = 0.20 // 20% delivered
	}

	numOrders := len(orders)
	numCancelled := int(float64(numOrders) * cancelledPercentage)
	numShipped := int(float64(numOrders) * shippedPercentage)
	numDelivered := int(float64(numOrders) * deliveredPercentage)

	// Shuffle orders to randomize which ones get updated
	shuffledIndices := r.Perm(numOrders)

	// Cancel some orders
	for i := 0; i < numCancelled; i++ {
		orderIndex := shuffledIndices[i]
		order := orders[orderIndex]

		url := fmt.Sprintf("%s/api/v1/orders/%s", baseURL, order.ID)
		req, err := http.NewRequest(http.MethodDelete, url, nil)
		if err != nil {
			return fmt.Errorf("failed to create cancel order request: %w", err)
		}

		resp, err := client.Do(req)
		if err != nil {
			return fmt.Errorf("failed to cancel order %s: %w", order.ID, err)
		}
		resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			return fmt.Errorf("failed to cancel order %s: status code %d", order.ID, resp.StatusCode)
		}

		log.Printf("Cancelled order %s", order.ID)
	}

	for i := numCancelled; i < numCancelled+numShipped; i++ {
		if i >= len(shuffledIndices) {
			break
		}

		orderIndex := shuffledIndices[i]
		order := orders[orderIndex]

		statusUpdate := OrderStatusUpdateDto{
			Status: "SHIPPED",
		}

		jsonData, err := json.Marshal(statusUpdate)
		if err != nil {
			return fmt.Errorf("failed to marshal order status update: %w", err)
		}

		url := fmt.Sprintf("%s/api/v1/orders/%s/status", baseURL, order.ID)
		req, err := http.NewRequest(http.MethodPut, url, bytes.NewBuffer(jsonData))
		if err != nil {
			return fmt.Errorf("failed to create status update request: %w", err)
		}

		req.Header.Set("Content-Type", "application/json")

		resp, err := client.Do(req)
		if err != nil {
			return fmt.Errorf("failed to update order %s status: %w", order.ID, err)
		}
		resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			return fmt.Errorf("failed to update order %s status: status code %d", order.ID, resp.StatusCode)
		}

		log.Printf("Updated order %s status to SHIPPED", order.ID)
	}

	for i := numCancelled; i < numCancelled+numDelivered; i++ {
		if i >= len(shuffledIndices) || i >= numCancelled+numShipped {
			break
		}

		orderIndex := shuffledIndices[i]
		order := orders[orderIndex]

		statusUpdate := OrderStatusUpdateDto{
			Status: "DELIVERED",
		}

		jsonData, err := json.Marshal(statusUpdate)
		if err != nil {
			return fmt.Errorf("failed to marshal order status update: %w", err)
		}

		url := fmt.Sprintf("%s/api/v1/orders/%s/status", baseURL, order.ID)
		req, err := http.NewRequest(http.MethodPut, url, bytes.NewBuffer(jsonData))
		if err != nil {
			return fmt.Errorf("failed to create status update request: %w", err)
		}

		req.Header.Set("Content-Type", "application/json")

		resp, err := client.Do(req)
		if err != nil {
			return fmt.Errorf("failed to update order %s status: %w", order.ID, err)
		}
		resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			return fmt.Errorf("failed to update order %s status: status code %d", order.ID, resp.StatusCode)
		}

		log.Printf("Updated order %s status to DELIVERED", order.ID)
	}

	log.Printf("Successfully updated statuses: %d cancelled, %d shipped, %d delivered",
		numCancelled, numShipped, numDelivered)
	return nil
}

func generateStreetName(r *rand.Rand) string {
	streets := []string{"Main", "Oak", "Pine", "Maple", "Cedar", "Elm", "Washington", "Lincoln", "Park", "Lake"}
	return streets[r.Intn(len(streets))]
}
