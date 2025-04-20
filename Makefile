build:
	docker compose build

build-no-cache:
	docker compose build --no-cache

run:
	docker compose up -d

run-it:
	docker compose up

stop:
	docker compose down

stop-volumes:
	docker compose down -v

test-command-service:
	docker compose exec command bash -c "cd /app && ./mvnw test"

format-command-service:
	docker compose exec command  -c "cd /app && ./mvnw spotless:apply"

format-query-service:
	docker compose exec query sh -c "go fmt ./... && go vet ./..."

seed-data:
	docker run --rm --network order-network -v $(pwd)/scripts/seed:/app -w /app golang:1.23-alpine go run .

.PHONY: build run run-it stop stop-volumes test-command-service format-command-service format-query-service seed-data
