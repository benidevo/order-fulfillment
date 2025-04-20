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
	"github.com/benidevo/order-fulfillment/query/internal/events/event_consumers"
	"github.com/benidevo/order-fulfillment/query/internal/events/event_handlers"
	"github.com/benidevo/order-fulfillment/query/internal/models"
	"github.com/benidevo/order-fulfillment/query/internal/repositories/mongodb"
	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type application struct {
	config            *config.Config
	router            *gin.Engine
	dbClient          *mongo.Client
	handlers          *handlers.Handlers
	inventoryConsumer *consumers.InventoryConsumer
	ordersConsumer    *consumers.OrderConsumer
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

	app.setupDependencies()
	if err := app.initializeIndexes(ctx); err != nil {
		return nil, fmt.Errorf("failed to initialize indexes: %w", err)
	}
	app.routes()

	return app, nil
}

// Run starts the application's HTTP server, Kafka consumer services, and sets
// up graceful shutdown handling.
func (app *application) Run() error {
	errChan := make(chan error, 1)

	go func() {
		log.Printf("Starting server on port %s", app.config.Port)
		if err := app.router.Run(":" + app.config.Port); err != nil {
			errChan <- err
		}
	}()

	go func() {
		log.Println("Starting Kafka consumer for inventory...")
		if err := app.inventoryConsumer.Start(context.Background()); err != nil {
			errChan <- fmt.Errorf("failed to start inventory consumer: %w", err)
		}
	}()

	go func() {
		log.Println("Starting Kafka consumer for orders...")
		if err := app.ordersConsumer.Start(context.Background()); err != nil {
			errChan <- fmt.Errorf("failed to start orders consumer: %w", err)
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

func (app *application) initializeIndexes(ctx context.Context) error {
	var err error
	db := app.dbClient.Database(app.config.DBName)

	_, err = db.Collection(models.InventoryCollectionName).Indexes().CreateMany(ctx, models.InventoryIndexes)
	if err != nil {
		return fmt.Errorf("failed to create inventory indexes: %w", err)
	}
	_, err = db.Collection(models.OrderCollectionName).Indexes().CreateMany(ctx, models.OrderIndexes)
	if err != nil {
		return fmt.Errorf("failed to create order indexes: %w", err)
	}
	return nil
}

func (app *application) setupDependencies() {
	db := app.dbClient.Database(app.config.DBName)

	inventoryRepo := mongodb.NewMongoInventoryRepository(db)
	orderRepo := mongodb.NewMongoOrderRepository(db)

	app.handlers = &handlers.Handlers{
		Inventory: handlers.NewInventoryHandler(inventoryRepo),
		Orders:    handlers.NewOrdersHandler(orderRepo),
	}

	inventoryEventHandler := eventhandlers.NewInventoryEventHandler(inventoryRepo)
	orderEventHandler := eventhandlers.NewOrderEventHandler(orderRepo)

	var err error
	app.inventoryConsumer, err = consumers.NewInventoryConsumer(app.config, inventoryEventHandler)
	if err != nil {
		log.Fatalf("failed to create inventory consumer: %v", err)
	}
	app.ordersConsumer, err = consumers.NewOrderConsumer(app.config, orderEventHandler)
	if err != nil {
		log.Fatalf("failed to create order consumer: %v", err)
	}
}

func (app *application) routes() {
	app.router.GET("/", func(c *gin.Context) {
		c.String(200, "Hello, world!")
	})

	api := app.router.Group("/api/v1")
	{
		inventory := api.Group("/inventory")
		{
			inventory.GET("", app.handlers.Inventory.ListInventory)
		}

		orders := api.Group("/orders")
		{
			orders.GET("", app.handlers.Orders.ListOrders)
			orders.GET("/:id", app.handlers.Orders.GetOrder)
		}
	}
}
