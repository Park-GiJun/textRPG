# Makefile for TextRPG

.PHONY: help build test run docker-up docker-down clean

# Default target
help:
	@echo "Available commands:"
	@echo "  make build       - Build the application"
	@echo "  make test        - Run tests"
	@echo "  make run         - Run the application locally"
	@echo "  make docker-up   - Start Docker containers"
	@echo "  make docker-down - Stop Docker containers"
	@echo "  make clean       - Clean build artifacts"
	@echo "  make migrate     - Run database migrations"

# Build the application
build:
	mkdir -p logs
	./gradlew clean build -x test

# Run tests
test:
	./gradlew test

# Run the application
run:
	./gradlew bootRun

# Start Docker containers
docker-up:
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	@sleep 10

# Stop Docker containers
docker-down:
	docker-compose down

# Clean build artifacts
clean:
	./gradlew clean
	rm -rf build/

# Run database migrations
migrate:
	./gradlew flywayMigrate

# Build Docker image
docker-build:
	docker build -t textrpg:latest .

# Run with Docker
docker-run: docker-up
	docker run -p 8080:8080 --network textrpg-network textrpg:latest

# Full setup
setup: docker-up migrate
	@echo "Setup complete! Run 'make run' to start the application."
