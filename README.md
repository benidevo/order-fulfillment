# Order Fulfillment System

A distributed order fulfillment system demonstrating CQRS and Event Sourcing patterns. This system showcases how these architectural patterns create scalable, maintainable enterprise applications using domain-driven design.

## System Context

This service operates as part of a larger e-commerce ecosystem:

1. **Upstream Services**: Submit finalized orders to this service
2. **Fulfillment Service (this system)**: Processes orders through their lifecycle
3. **Downstream Consumers**: Can subscribe to events for notifications, shipping, etc.

## Architecture Overview

```
                                   ┌───────────────────┐
                                   │                   │
                          ┌───────▶│  Command Service  │
                          │        │  (Java/Spring)    │
                          │        │                   │
┌──────────┐       ┌──────────┐    └─────────┬─────────┘
│          │       │          │              │
│  Client  │──────▶│  Nginx   │              │ Publishes Events
│          │       │ Gateway  │              │
└──────────┘       └──────────┘              ▼
                          │        ┌───────────────────┐
                          │        │                   │
                          │        │   Kafka Events    │
                          │        │                   │
                          │        └─────────┬─────────┘
                          │                  │
                          │                  │ Consumes Events
                          │        ┌─────────▼─────────┐
                          │        │                   │
                          └───────▶│   Query Service   │──────┐
                                   │      (Go)         │      │
                                   │                   │      │
                                   └───────────────────┘      │
                                                              │
                                                              │
                                                              ▼
                                                    ┌───────────────┐
                                                    │               │
                                                    │   MongoDB     │
                                                    │               │
                                                    └───────────────┘
```

### Command Service (Java/Spring Boot)

- Processes all write operations through commands
- Validates business rules within domain aggregates
- Generates events for state changes
- Uses in-memory event store with Kafka publishing

### Event Bus (Kafka)

- Distributes events between services
- Maintains ordered event streams
- Enables event replay capabilities

### Query Service (Go)

- Consumes events to build read models in MongoDB
- Provides optimized query endpoints
- Implements eventual consistency

## Key Design Patterns

### CQRS (Command Query Responsibility Segregation)

The system separates write operations (commands) from read operations (queries), allowing for optimization of each path independently.

### Event Sourcing

All state changes are captured as a sequence of events that serve as the system's source of truth. Current state is derived by replaying events, providing a complete audit history.

### Domain-Driven Design

The system is built around domain aggregates (Order, Inventory) with clear boundaries, using value objects (Money, Address) and a ubiquitous language throughout the codebase.

### Domain Model

**Orders** follow a state machine:

- REGISTERED → SHIPPED/PARTIALLY_SHIPPED → DELIVERED/PARTIALLY_DELIVERED
- REGISTERED → CANCELLED

**Inventory** tracks:

- Available Quantity
- Allocated Quantity (to orders)
- Status (AVAILABLE, OUT_OF_STOCK, DISCONTINUED)

## Setup and Running

### Prerequisites

- Docker and Docker Compose
- Make (optional)

### Quick Start

1. Clone the repository and create environment files:

```
git clone https://github.com/benidevo/order-fulfillment.git
cd order-fulfillment
```

```
cp command/env.example command/.env
cp query/env.example query/.env
```

1. Build and start services:

```
make build
make run
```

Or without Make:

```
docker compose build
docker compose up -d
```

1. Seed test data:

```
make seed-data
```

### Accessing the System

- **API Gateway**: <http://localhost:8000/api/v1>
- **API Documentation**:
  - Swagger UI: <http://localhost:8000/api-docs>
  - ReDoc: <http://localhost:8000/redoc/>
- **Kafka UI (Kafdrop)**: <http://localhost:9000>

## Technical Stack

- **Command Service**: Java 17+, Spring Boot, Spring Kafka
- **Query Service**: Go 1.23+, Gin, MongoDB driver
- **Event Bus**: Apache Kafka, Zookeeper
- **Database**: MongoDB (for query projections)
- **Gateway**: Nginx
- **Infrastructure**: Docker, Docker Compose

## Available Make Commands

- `make build`: Build all services
- `make run`: Start all services
- `make stop`: Stop all services
- `make seed-data`: Populate with test data

Refer to the [API documentation](http://localhost:8000/api-docs) for details on available endpoints and request/response formats.
