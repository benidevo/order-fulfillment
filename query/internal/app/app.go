package app

import (
	"context"
	"fmt"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/benidevo/order-fulfillment/query/internal/api/handlers"
	"github.com/benidevo/order-fulfillment/query/internal/config"
	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type application struct {
	config   *config.Config
	router   *gin.Engine
	dbClient *mongo.Client
}

// Creates and returns a new application instance based on the provided configuration.
// It initializes the Gin router, establishes a connection to the MongoDB database
// using the configuration's MongoURI, and sets up the application routes.
// Returns a pointer to the initialized application and any error encountered during setup.
// The MongoDB connection is attempted with a 10-second timeout.
func New(cfg *config.Config) (*application, error) {
	app := &application{
		config: cfg,
		router: gin.Default(),
	}

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	client, err := mongo.Connect(ctx, options.Client().ApplyURI(cfg.MongoURI))
	if err != nil {
		return nil, err
	}
	app.dbClient = client

	app.routes()

	return app, nil
}

func (app *application) routes() {
	app.router.GET("/", func(c *gin.Context) {
		c.String(200, "Hello, world!")
	})

	api := app.router.Group("/api/v1")
	{
		inventory := api.Group("/inventory")
		inventoryHandler := handlers.NewInventoryHandler()
		{
			inventory.GET("", inventoryHandler.ListInventory)
		}

		orders := api.Group("/orders")
		ordersHandler := handlers.NewOrdersHandler()
		{
			orders.GET("", ordersHandler.ListOrders)
			orders.GET("/:id", ordersHandler.GetOrder)
		}
	}
}

// Run starts the application server on the configured port and handles graceful shutdown.
// It creates a separate goroutine to run the HTTP server and waits for either server errors
// or termination signals (SIGINT or SIGTERM). When a termination signal is received,
// the database connection is gracefully closed within a 10-second timeout.
// Returns an error if the server encounters an error during startup or if database
// disconnection fails.
func (app *application) Run() error {

	errChan := make(chan error, 1)

	go func() {
		log.Printf("Starting server on port %s", app.config.Port)
		if err := app.router.Run(":" + app.config.Port); err != nil {
			errChan <- err
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)

	select {
	case err := <-errChan:
		return fmt.Errorf("server error: %w", err)
	case <-quit:
		log.Println("Shutting down server gracefully...")
	}

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	return app.dbClient.Disconnect(ctx)
}
