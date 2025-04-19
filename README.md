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
- Uses in-memory event store with Kafka publishing
- Maintains proper aggregate boundaries (Order, Inventory)

### Event Bus (Kafka)

- Serves as the communication channel between components
- Provides event streams for building read models
- Enables event replay capabilities
- Facilitates eventual consistency

### Query Side (Planned - Node.js)

- Will consume events to build and maintain read models
- Will provide efficient query endpoints
- Will demonstrate eventual consistency from command side

## Key Concepts Demonstrated

- **Event Sourcing**: Using events as the source of truth
- **CQRS**: Separating read and write responsibilities
- **Domain-Driven Design**: Proper aggregate boundaries and invariants
- **Event-Driven Architecture**: Loose coupling through events
- **Eventual Consistency**: Asynchronous propagation of changes

## Project Features

- **Order Management**: Create, cancel, and update order status
- **Inventory Control**: Allocate and return inventory for orders
- **Event Publishing**: All state changes published as events
- **Partial Fulfillment**: Orders can be partially fulfilled based on inventory

## Technical Stack

- **Command Service**: Java 17, Spring Boot, Spring Kafka
- **Event Store**: Apache Kafka + In-memory storage
- **Query Service**: Node.js, Express (planned)
- **Infrastructure**: Docker, Docker Compose
- **Build Tool**: Maven

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

5. Kafka management UI (Kafdrop) will be available at:

   ```
   http://localhost:9000
   ```

### Available Commands

The following make commands are available:

- `make build`: Build all services
- `make run`: Start all services in detached mode
- `make run-it`: Start all services in interactive mode (shows logs)
- `make stop`: Stop all services
- `make format-command-service`: Format code in the command service

## API Endpoints

### Command Service

```
POST /api/v1/orders                      # Register a new order
PUT /api/v1/orders/{orderId}/status      # Update order status
DELETE /api/v1/orders/{orderId}          # Cancel an order
PUT /api/v1/inventory/{productId}        # Update inventory quantity
POST /api/v1/inventory/{productId}/allocate  # Manually allocate inventory
POST /api/v1/inventory/{productId}/return    # Manually return inventory
```

### Query Service (Planned)

```
GET /api/inventory                    # View current inventory levels
GET /api/orders/{orderId}             # View order details
GET /api/orders?status=               # List orders by status
```

## Understanding the Codebase

### Command Side Structure

- **API Layer**: REST controllers in `api` package
- **Command Layer**: Command objects and handlers
- **Domain Layer**: Aggregates and value objects
- **Event Layer**: Event definitions and publishing
- **Repository Layer**: Event storage and reconstruction

### Key Design Decisions

1. **In-Memory Storage**: For simplicity and educational purposes
2. **Independent Aggregates**: Order and Inventory are separate boundaries
3. **Event Publishing**: All state changes are recorded as events
4. **Partial Fulfillment**: Orders attempt to allocate each item individually
