# Product Requirements Document: Order Fulfillment Service

## Project Overview

### Problem Statement

In e-commerce systems, order fulfillment requires accurate inventory tracking, efficient order processing, and reliable status updates. Traditional monolithic approaches often struggle with complexity, scalability, and maintaining historical context of operations. This project demonstrates how Event Sourcing and CQRS architectural patterns can address these challenges while providing a more maintainable and extensible architecture.

### Project Goal

Create an order fulfillment service using Event Sourcing and CQRS (Command Query Responsibility Segregation) patterns. The system uses Java and Spring Boot for the command service and a Go-based query service, connected through Kafka as an event bus. This approach showcases how these patterns facilitate domain separation, enable event-driven architecture, and provide the benefits of specialized read and write models.

### Educational Focus

This project serves as a learning tool for understanding advanced architectural patterns rather than a production-ready system. It prioritizes clarity of concepts and implementation over enterprise-grade features, using in-memory storage with event publishing to demonstrate eventual consistency.

### Scope

The project includes:

- **Command Processing**: Handling write operations through explicit commands (create, update, cancel)
- **Domain Enforcement**: Maintaining business rules and invariants within domain aggregates
- **Event Generation**: Recording all state changes as immutable events
- **Event Publishing**: Distributing events through Kafka to enable event-driven architecture
- **Event Sourcing**: Using events as the source of truth for system state
- **Inventory Management**: Allocating and returning inventory based on order changes
- **Order Lifecycle Management**: Tracking orders through their fulfillment stages
- **Polyglot Implementation**: Demonstrating how different languages can work together in a CQRS system

### Out of Scope

- Authentication and authorization
- Production-grade performance optimizations
- User interfaces
- Advanced error recovery mechanisms
- Comprehensive analytics
- Payment processing
- Customer management

## System Architecture

The system implements a clean CQRS architecture with the following components:

### Command Side (Java/Spring Boot)

- RESTful API for receiving commands (register order, update status, cancel, inventory management)
- Command handlers for validation and domain rule enforcement
- Domain models with proper aggregate boundaries (Order and Inventory)
- Event generation and publishing to Kafka
- In-memory event store with optimistic concurrency control
- Comprehensive exception handling

### Event Bus (Kafka)

- Central event distribution mechanism
- Maintains ordered event streams by aggregate ID
- Enables eventual consistency between command and query sides
- Provides event replay capabilities
- Decouples command and query services

### Query Side (Go)

- Event consumers for building read-optimized projections (in development)
- MongoDB for storing read models
- RESTful API for efficient query operations
- Multiple specialized read models based on query requirements
- Independent scaling from command side

## Domain Models

### Order Aggregate

- Core entity representing customer orders
- Maintains items, addresses, and status
- Enforces business rules for status transitions
- Generates events for state changes
- Maintains invariants during operations

### Inventory Aggregate

- Manages product availability
- Handles allocation and returns
- Enforces inventory constraints
- Maintains separation from Order aggregate
- Prevents negative inventory and over-allocation

## Core Events

### Order Events

- **OrderCreated**: Generated when a new order is registered
- **OrderStatusUpdated**: Generated when an order transitions between states
- **OrderCancelled**: Generated when an order is cancelled

### Inventory Events

- **InventoryUpdated**: Generated when stock levels change
- **InventoryAllocated**: Generated when inventory is reserved for an order
- **InventoryReturned**: Generated when inventory is returned from cancelled orders

## Query Models (Planned)

- **OrderSummary**: Current state of all orders with essential details
- **InventoryStatus**: Current inventory levels across products
- **FulfillmentWorkQueue**: Orders ready for processing by status
- **CustomerOrderHistory**: Order history organized by customer

## Technical Stack

- **Command Side**: Java 17, Spring Boot, Spring Kafka, Lombok
- **Event Bus**: Apache Kafka, Zookeeper
- **Query Side**: Go, Gin framework, KafkaJS
- **Storage**: In-memory with Kafka as event log, MongoDB for query models
- **Infrastructure**: Docker, Docker Compose for local development

## Metrics of Success

The project will be considered successful when:

1. Commands properly update system state and generate appropriate events
2. The query side builds and maintains projections from the event stream
3. The system demonstrates eventual consistency between command and query sides
4. Events can be replayed to rebuild system state
5. The polyglot architecture demonstrates language-appropriate implementations
6. Documentation clearly explains architectural concepts and implementation details
