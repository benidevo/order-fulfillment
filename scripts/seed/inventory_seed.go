package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"log"
	"math/big"
	"math/rand"
	"net/http"
)

// ProductCategory represents a product category with its base name and price range
type ProductCategory struct {
	Name     string
	MinPrice float64
	MaxPrice float64
	MinStock int
	MaxStock int
}

// Categories of products to generate
var productCategories = []ProductCategory{
	{Name: "Laptop", MinPrice: 800.0, MaxPrice: 2500.0, MinStock: 5, MaxStock: 20},
	{Name: "Smartphone", MinPrice: 300.0, MaxPrice: 1200.0, MinStock: 10, MaxStock: 50},
	{Name: "Tablet", MinPrice: 200.0, MaxPrice: 1000.0, MinStock: 8, MaxStock: 30},
	{Name: "Monitor", MinPrice: 150.0, MaxPrice: 800.0, MinStock: 7, MaxStock: 25},
	{Name: "Keyboard", MinPrice: 30.0, MaxPrice: 150.0, MinStock: 15, MaxStock: 60},
	{Name: "Mouse", MinPrice: 15.0, MaxPrice: 100.0, MinStock: 20, MaxStock: 80},
	{Name: "Headphones", MinPrice: 25.0, MaxPrice: 350.0, MinStock: 12, MaxStock: 45},
	{Name: "Printer", MinPrice: 100.0, MaxPrice: 500.0, MinStock: 4, MaxStock: 15},
	{Name: "Camera", MinPrice: 200.0, MaxPrice: 1500.0, MinStock: 6, MaxStock: 18},
	{Name: "Speaker", MinPrice: 50.0, MaxPrice: 400.0, MinStock: 10, MaxStock: 40},
}

// GenerateProducts generates a list of products based on the configured categories
func GenerateProducts(numProducts int, r *rand.Rand) []Product {
	products := make([]Product, 0, numProducts)

	for _, category := range productCategories {
		if len(products) >= numProducts {
			break
		}

		price := r.Float64()*(category.MaxPrice-category.MinPrice) + category.MinPrice
		stock := r.Intn(category.MaxStock-category.MinStock+1) + category.MinStock

		productID := fmt.Sprintf("prod-%s-%03d", category.Name, len(products)+1)
		productName := fmt.Sprintf("%s %s %d",
			generateBrand(r),
			category.Name,
			2020+r.Intn(6)) // Year between 2020-2025

		products = append(products, Product{
			ID:    productID,
			Name:  productName,
			Price: big.NewFloat(roundToTwoDecimals(price)),
			Stock: stock,
		})
	}

	for len(products) < numProducts {
		category := productCategories[r.Intn(len(productCategories))]

		price := r.Float64()*(category.MaxPrice-category.MinPrice) + category.MinPrice
		stock := r.Intn(category.MaxStock-category.MinStock+1) + category.MinStock

		productID := fmt.Sprintf("prod-%s-%03d", category.Name, len(products)+1)
		productName := fmt.Sprintf("%s %s %d",
			generateBrand(r),
			category.Name,
			2020+r.Intn(6)) // Year between 2020-2025

		products = append(products, Product{
			ID:    productID,
			Name:  productName,
			Price: big.NewFloat(roundToTwoDecimals(price)),
			Stock: stock,
		})
	}

	return products
}

// SeedInventory seeds the inventory with the generated products
func SeedInventory(baseURL string, products []Product) error {
	client := &http.Client{}

	log.Println("Seeding inventory...")

	for _, product := range products {
		inventoryUpdate := InventoryUpdateDto{
			Quantity: product.Stock,
		}

		jsonData, err := json.Marshal(inventoryUpdate)
		if err != nil {
			return fmt.Errorf("failed to marshal inventory update: %w", err)
		}

		url := fmt.Sprintf("%s/api/v1/inventory/%s", baseURL, product.ID)
		req, err := http.NewRequest(http.MethodPut, url, bytes.NewBuffer(jsonData))
		if err != nil {
			return fmt.Errorf("failed to create inventory update request: %w", err)
		}

		req.Header.Set("Content-Type", "application/json")

		resp, err := client.Do(req)
		if err != nil {
			return fmt.Errorf("failed to update inventory for product %s: %w", product.ID, err)
		}
		defer resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			return fmt.Errorf("failed to update inventory for product %s: status code %d", product.ID, resp.StatusCode)
		}

		log.Printf("Created inventory for product %s (%s) with %d units", product.ID, product.Name, product.Stock)
	}

	log.Printf("Successfully seeded %d inventory items", len(products))
	return nil
}

func generateBrand(r *rand.Rand) string {
	brands := []string{"TechPro", "DigitalX", "NexGen", "InnoTech", "SmartWare",
		"UltraLink", "MaxiTech", "PrimeTech", "EliteCore", "VisionTech"}
	return brands[r.Intn(len(brands))]
}

func roundToTwoDecimals(value float64) float64 {
	return float64(int(value*100)) / 100
}
