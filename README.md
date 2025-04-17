# Order Manager

A simplified order management system demonstrating Event Sourcing and CQRS (Command Query Responsibility Segregation) patterns.

## Project Overview

This project implements a simplified order management system that showcases how Event Sourcing and CQRS patterns can address common challenges in traditional systems, including data consistency issues, audit capabilities, and maintaining the historical context of order lifecycles.

The system allows basic order creation, modification, and querying with a complete event history, making it ideal for learning these architectural patterns in a practical context.

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

- **Product Catalog Management**: Add and update products with inventory tracking
- **Order Processing**: Create, update, and cancel orders with status tracking
- **Inventory Management**: Track inventory levels with automatic adjustments based on orders
- **Querying Capabilities**: View orders, inventory, and sales statistics

## Technical Stack

- **Command Service**: Java 17, Spring Boot, Spring Kafka
- **Event Store**: Apache Kafka
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
   git clone https://github.com/benidevo/order-manager.git
   cd order-manager
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

## Event Types

The system uses the following core event types:

- **Product Events**: ProductCreated, ProductUpdated, InventoryAdjusted
- **Order Events**: OrderCreated, OrderItemAdded, OrderItemRemoved, OrderItemQuantityChanged, OrderConfirmed, OrderCancelled, OrderShipped, OrderDelivered
