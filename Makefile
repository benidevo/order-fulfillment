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

format-command-service:
	docker compose exec command bash -c "cd /app && ./mvnw spotless:apply"

format-query-service:
	docker compose exec query sh -c "go fmt ./... && go vet ./..."

seed-data:
	docker compose run seed sh -c "go run ."

format-seed-service:
	docker compose run seed sh -c "go fmt ./... && go vet ./..."

.PHONY: build run run-it stop stop-volumes test-command-service format-command-service format-query-service seed-data format-seed-service
