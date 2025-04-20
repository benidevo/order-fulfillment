# Order Fulfillment Data Seeder

This seeds the Order Fulfillment system with realistic test data, including products, inventory, customers, and orders in various states.

## Features

- Creates products across multiple categories with realistic prices
- Sets up inventory levels for all products
- Generates customers with addresses
- Creates orders with multiple items
- Updates order statuses (cancelled, shipped, delivered) to create a realistic distribution
- Configurable via environment variables or command-line flags

## Usage

### Running with Default Configuration

Simply run the script with no arguments to use the default configuration:

```bash
go run ./scripts/seed
```

This will seed the system with 10 products and 20 orders using the API at `http://localhost:80`.

### Configuration Options

You can customize the seeding process through environment variables:

| Environment Variable | Description | Default Value |
|---|---|---|
| `SEED_BASE_URL` | Base URL of the API gateway | `http://localhost:80` |
| `SEED_NUM_PRODUCTS` | Number of products to create | `10` |
| `SEED_NUM_ORDERS` | Number of orders to create | `20` |
| `SEED_RANDOM_SEED` | Seed for random number generator | `42` |
| `SEED_ENABLE_CANCEL` | Enable cancelling some orders | `true` |
| `SEED_ENABLE_SHIP` | Enable shipping some orders | `true` |
| `SEED_ENABLE_DELIVER` | Enable delivering some shipped orders | `true` |

Example:

```bash
SEED_BASE_URL=http://localhost:80 SEED_NUM_PRODUCTS=20 SEED_NUM_ORDERS=50 go run ./scripts/seed
```

## Building

To build a standalone executable:

```bash
cd scripts/seed
go build -o seed-tool
```

Then run the tool:

```bash
./seed-tool
```

## Docker Usage

You can also run the seeder from within the Docker network to ensure it can access all services:

```bash
docker run --rm --network order-network -v $(pwd)/scripts/seed:/app -w /app golang:1.23-alpine go run .
```

## Notes

- The random seed ensures reproducible data generation. Use the same seed to get the same dataset.
- It creates data through the command service API, which then propagates to the query service through events.
- Allow a few seconds after running for all events to propagate through Kafka to the query service.
