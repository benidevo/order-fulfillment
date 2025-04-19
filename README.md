# Order Fulfillment Service

A simplified order fulfillment service demonstrating Event Sourcing and CQRS (Command Query Responsibility Segregation) patterns.

## Project Overview

This project implements an order fulfillment service that showcases how Event Sourcing and CQRS patterns can address common challenges in traditional systems, including:

- Maintaining complete audit history
- Separating read and write concerns
- Enabling specialized data models for different operations
- Building an event-driven architecture
- Establishing clear domain boundaries

The service handles the fulfillment lifecycle of orders, including inventory management, status tracking, and state transitions. It maintains a complete event history that serves as the source of truth, making it ideal for understanding these architectural patterns in practice.

## System Context

This service operates as part of a larger e-commerce ecosystem:

1. **Upstream Services**: Submit finalized orders to our service
2. **Fulfillment Service (this system)**: Processes orders through their lifecycle
3. **Downstream Consumers**: Can subscribe to our events for notifications, shipping, etc.

## Architecture

The system implements CQRS and Event Sourcing.

### Command Side (Java/Spring Boot)

- Processes all write operations through explicit commands
- Validates business rules within domain aggregates
- Generates events for all state changes
- Built with Spring Boot and Java 17
- Uses an in-memory event store with Kafka publishing
- Enforces proper aggregate boundaries (Order, Inventory)

### Event Bus (Kafka)

- Distributes events between services
- Maintains ordered event streams
- Enables event replay capabilities
- Facilitates eventual consistency

### Query Side (Go)

- Consumes events to build and maintain read models (in development)
- Uses MongoDB for storing projections
- Provides efficient query endpoints
- Demonstrates eventual consistency from command side

## Key Patterns Demonstrated

- **Event Sourcing**: Using events as the system's source of truth
- **CQRS**: Separating read and write responsibilities
- **Domain-Driven Design**: Enforcing business rules within proper aggregate boundaries
- **Event-Driven Architecture**: Loosely coupling components through events
- **Polyglot Persistence**: Using appropriate storage technologies for different needs
- **Eventual Consistency**: Asynchronous propagation of state changes

## Project Features

- **Order Management**: Create, cancel, and update order status
- **Inventory Control**: Allocate and return inventory for orders
- **Event Publishing**: All state changes published as events
- **Partial Fulfillment**: Orders can be partially fulfilled based on inventory
- **Status Transitions**: Rules-based order status progression

## Technical Stack

- **Command Service**: Java 17, Spring Boot, Spring Kafka, Lombok
- **Event Bus**: Apache Kafka, Zookeeper
- **Query Service**: Go, Gin framework, MongoDB driver
- **Infrastructure**: Docker, Docker Compose
- **Build Tools**: Maven (Java), Go Modules (Go)

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

2. Create `.env` files in both command and query directories:

   ```
   cp command/env.example command/.env
   cp query/env.example query/.env
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

5. The query service will be available at:

   ```
   http://localhost:8081
   ```

6. Kafka management UI (Kafdrop) will be available at:

   ```
   http://localhost:9000
   ```

### Available Commands

The following make commands are available:

- `make build`: Build all services
- `make build-no-cache`: Build all services without using cache
- `make run`: Start all services in detached mode
- `make run-it`: Start all services in interactive mode (shows logs)
- `make stop`: Stop all services
- `make stop-volumes`: Stop services and remove volumes
- `make test-command-service`: Run tests for the command service
- `make format-command-service`: Format code in the command service
- `make format-query-service`: Format code in the query service

## API Endpoints

### Command Service

```
POST /api/v1/orders                       # Register a new order
PUT /api/v1/orders/{orderId}/status       # Update order status
DELETE /api/v1/orders/{orderId}           # Cancel an order
PUT /api/v1/inventory/{productId}         # Update inventory quantity
POST /api/v1/inventory/{productId}/allocate  # Manually allocate inventory
POST /api/v1/inventory/{productId}/return    # Manually return inventory
```

### Query Service (Under Development)

```
GET /api/v1/inventory                  # View current inventory levels
GET /api/v1/orders/{orderId}           # View order details
GET /api/v1/orders                     # List orders with filtering options
```

## Understanding the Codebase

### Command Side Structure

- **API Layer**: REST controllers in `api` package
- **Command Layer**: Command objects and handlers
- **Domain Layer**: Aggregates and value objects
- **Event Layer**: Event definitions and publishing
- **Repository Layer**: Event storage and reconstruction

### Query Side Structure (In Development)

- **API Layer**: REST handlers
- **Consumers**: Kafka consumers for processing events
- **Projections**: Read models built from events
- **Repositories**: MongoDB data access

### Key Design Decisions

1. **Polyglot Architecture**: Using appropriate languages for each component
2. **In-Memory Command Storage**: For simplicity and educational purposes
3. **MongoDB Query Storage**: For efficient read operations
4. **Independent Aggregates**: Order and Inventory as separate boundaries
5. **Event Publishing**: All state changes recorded as events
6. **Optimistic Concurrency**: For handling concurrent operations

## Development Status

- **Command Service**: Fully implemented with core business logic
- **Event Bus**: Fully configured and operational
- **Query Service**: Basic structure implemented, event consumers and projections in development
