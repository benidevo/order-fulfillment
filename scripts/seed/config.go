package main

import (
	"log"
	"os"
	"strconv"
)

// Config holds the configuration for the seed script
type Config struct {
	BaseURL       string
	NumProducts   int
	NumOrders     int
	RandomSeed    int64
	EnableCancel  bool
	EnableShip    bool
	EnableDeliver bool
}

// LoadConfig loads the configuration from environment variables or uses defaults
func LoadConfig() Config {
	cfg := Config{
		BaseURL:       getEnvOr("SEED_BASE_URL", "http://nginx:80"),
		NumProducts:   getEnvIntOr("SEED_NUM_PRODUCTS", 10),
		NumOrders:     getEnvIntOr("SEED_NUM_ORDERS", 20),
		RandomSeed:    getEnvInt64Or("SEED_RANDOM_SEED", 42),
		EnableCancel:  getEnvBoolOr("SEED_ENABLE_CANCEL", true),
		EnableShip:    getEnvBoolOr("SEED_ENABLE_SHIP", true),
		EnableDeliver: getEnvBoolOr("SEED_ENABLE_DELIVER", true),
	}
	return cfg
}

func getEnvOr(key, defaultValue string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return defaultValue
}

func getEnvIntOr(key string, defaultValue int) int {
	if value, exists := os.LookupEnv(key); exists {
		intValue, err := strconv.Atoi(value)
		if err != nil {
			log.Printf("Warning: could not parse %s=%s as integer, using default: %d", key, value, defaultValue)
			return defaultValue
		}
		return intValue
	}
	return defaultValue
}

func getEnvInt64Or(key string, defaultValue int64) int64 {
	if value, exists := os.LookupEnv(key); exists {
		int64Value, err := strconv.ParseInt(value, 10, 64)
		if err != nil {
			log.Printf("Warning: could not parse %s=%s as int64, using default: %d", key, value, defaultValue)
			return defaultValue
		}
		return int64Value
	}
	return defaultValue
}

func getEnvBoolOr(key string, defaultValue bool) bool {
	if value, exists := os.LookupEnv(key); exists {
		boolValue, err := strconv.ParseBool(value)
		if err != nil {
			log.Printf("Warning: could not parse %s=%s as boolean, using default: %t", key, value, defaultValue)
			return defaultValue
		}
		return boolValue
	}
	return defaultValue
}
