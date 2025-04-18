# Order Fulfillment Service

A simplified order fulfillment service demonstrating Event Sourcing and CQRS (Command Query Responsibility Segregation) patterns.

## Project Overview

This project implements a specialized order fulfillment service that showcases how Event Sourcing and CQRS patterns can address common challenges in traditional systems, including data consistency issues, audit capabilities, and maintaining the historical context of fulfillment operations.

The service receives already placed orders from upstream services, verifies inventory availability, and manages the order through the fulfillment lifecycle. It maintains a complete event history, making it ideal for understanding these architectural patterns in a practical context.

## System Context

This service operates as part of a larger e-commerce ecosystem:

1. **Upstream Services**: Submit finalized, paid orders to our service
2. **Product/Inventory Service**: Provides inventory updates through events
3. **Fulfillment Service (this system)**: Processes orders through the fulfillment lifecycle
4. **Downstream Consumers**: May subscribe to our fulfillment events (shipping, notifications)

## Architecture

The system follows an Event Sourcing and CQRS architecture with the following components:

### Command Side (Java)

- Processes all write operations through commands
- Validates commands and generates domain events
- Built with Spring Boot and Java 17
- Publishes events to Kafka

### Event Store (Kafka)

- Serves as the source of truth for the system
- Stores all events with appropriate retention
- Provides event streams for building read models

### Query Side (Node.js - Planned)

- Consumes events to build and maintain read models
- Provides efficient query capabilities
- Optimizes data structures for specific query patterns

## Features

- **Order Registration**: Receive finalized orders from upstream services
- **Inventory Management**: Track inventory levels and allocate to orders
- **Fulfillment Tracking**: Process orders through confirmation, shipping, and delivery
- **Order Cancellation**: Process cancellations and return inventory
- **Inventory Projection**: Maintain a view of available inventory from product service events

## Technical Stack

- **Command Service**: Java 17, Spring Boot, Spring Kafka
- **Event Store**: Apache Kafka
- **Query Service**: Node.js, Express, MongoDB (planned)
- **Infrastructure**: Docker, Docker Compose
- **Build Tool**: Maven
- **Code Quality**: Spotless

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Make (optional, but recommended)

### Setup

1. Clone the repository:

   ```
   git clone https://github.com/benidevo/order-fulfillment.git
   cd order-fulfillment
   ```

2. Create `.env` file in the command directory:

   ```
   cp command/env.example command/.env
   ```

3. Build and start the services:

   ```
   make build
   make run
   ```

   Alternatively, without Make:

   ```
   docker compose build
   docker compose up -d
   ```

4. The command service will be available at:

   ```
   http://localhost:8080
   ```

### Available Commands

The following make commands are available:

- `make build`: Build all services
- `make build-no-cache`: Build all services without using cache
- `make run`: Start all services in detached mode
- `make run-it`: Start all services in interactive mode (shows logs)
- `make stop`: Stop all services
- `make stop-volumes`: Stop all services and remove volumes
- `make test-command-service`: Run tests for the command service
- `make format-command-service`: Format code in the command service using Spotless

## API Endpoints

### Command Service

```
POST /api/orders                      # Register a finalized order
PUT /api/orders/{orderId}/status      # Update order status
DELETE /api/orders/{orderId}          # Cancel an order
```

### Query Service (Planned)

```
GET /api/inventory                    # View current inventory levels
GET /api/orders/{orderId}             # View order details
GET /api/orders?status=               # List orders by status
```

## Event Model

The system uses the following core event types:

### Inventory Events

- `InventoryUpdated` - When product inventory changes (from Product Service)
- `InventoryAllocated` - When inventory is assigned to an order
- `InventoryReturned` - When inventory is returned after cancellation

### Order Events

- `OrderReceived` - When an order is first received from upstream
- `OrderRegistered` - When an order passes validation and is accepted
- `OrderRejected` - When an order cannot be fulfilled (e.g., insufficient inventory)
- `OrderConfirmed` - When fulfillment process begins
- `OrderShipped` - When order is shipped to customer
- `OrderDelivered` - When delivery is confirmed
- `OrderCancelled` - When an order is cancelled
