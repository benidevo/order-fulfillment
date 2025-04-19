package config

import (
	"os"
)

// Config represents the configuration for the application.
type Config struct {
	KafkaBrokers string
	MongoURI     string
	Port         string
	DBName       string
}

// Load retrieves configuration settings from environment variables.
// It returns a new Config instance with values loaded from the environment,
// using default values when environment variables are not set.
//
// Returns:
//   - *Config: A pointer to the populated configuration struct
//   - error: Currently always nil, but maintained for future error handling capabilities
func Load() (*Config, error) {
	return &Config{
		KafkaBrokers: getEnv("KAFKA_BROKERS", "localhost:9092"),
		MongoURI:     getEnv("MONGODB_URI", "mongodb://localhost:27017"),
		Port:         getEnv("PORT", "8081"),
		DBName:       getEnv("DB_NAME", "order-fulfillment"),
	}, nil
}

func getEnv(key, defaultValue string) string {
	value := os.Getenv(key)
	if value == "" {
		return defaultValue
	}
	return value
}
