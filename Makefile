.PHONY: help check-prereqs install-prereqs install clean build test run dev stop docker-up docker-down logs setup start-app

RED=\033[0;31m
GREEN=\033[0;32m
YELLOW=\033[1;33m
BLUE=\033[0;34m
NC=\033[0m

help:
	@echo "🚀 Customer Registration System - Available Commands:"
	@echo ""
	@echo "$(BLUE)Setup (One-time):$(NC)"
	@echo "  make setup       - 🎯 FULL SETUP: Check prereqs → Install → Start DB → Build → Run"
	@echo "  make check-prereqs - Check if Java 17, Maven, Docker are installed"
	@echo ""
	@echo "$(BLUE)Development:$(NC)"
	@echo "  make start-app   - 🚀 START: Start database → Install deps → Run app"
	@echo "  make install     - Install Maven dependencies"
	@echo "  make build       - Build the application"
	@echo "  make test        - Run unit tests"
	@echo "  make integration - Run integration tests"
	@echo "  make run         - Run the application"
	@echo "  make dev         - Start in development mode"
	@echo ""
	@echo "$(BLUE)Docker Services:$(NC)"
	@echo "  make docker-up   - Start all services (PostgreSQL + Elasticsearch + MailHog)"
	@echo "  make docker-down - Stop all services"
	@echo ""
	@echo "$(BLUE)Utilities:$(NC)"
	@echo "  make clean       - Clean build artifacts"
	@echo "  make logs        - Show application logs"
	@echo "  make stop        - Stop running application"
	@echo "  make status      - Check application status"
	@echo ""

check-prereqs:
	@echo "$(BLUE)🔍 Checking prerequisites...$(NC)"
	@echo ""
	@echo "$(YELLOW)Checking Java 17...$(NC)"
	@if command -v java >/dev/null 2>&1; then \
		JAVA_VERSION=$$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1); \
		if [ "$$JAVA_VERSION" = "1" ]; then \
			FULL_VERSION=$$(java -version 2>&1 | head -n 1); \
			if echo "$$FULL_VERSION" | grep -q "1\.8\|17\."; then \
				MAJOR_VERSION=$$(echo "$$FULL_VERSION" | sed 's/.*"\(.*\)".*/\1/' | cut -d'.' -f2); \
				if [ "$$MAJOR_VERSION" -ge 17 ]; then \
					echo "$(GREEN)✅ Java $$MAJOR_VERSION found: $$FULL_VERSION$(NC)"; \
				else \
					echo "$(RED)❌ Java 17+ required, found: $$FULL_VERSION$(NC)"; \
					echo "$(YELLOW)Please install Java 17 or above: https://adoptium.net/$(NC)"; \
					exit 1; \
				fi; \
			else \
				echo "$(RED)❌ Java 17+ required, found: $$FULL_VERSION$(NC)"; \
				echo "$(YELLOW)Please install Java 17 or above: https://adoptium.net/$(NC)"; \
				exit 1; \
			fi; \
		elif [ "$$JAVA_VERSION" -ge 17 ]; then \
			FULL_VERSION=$$(java -version 2>&1 | head -n 1); \
			echo "$(GREEN)✅ Java $$JAVA_VERSION found: $$FULL_VERSION$(NC)"; \
		else \
			echo "$(RED)❌ Java 17+ required, found version $$JAVA_VERSION$(NC)"; \
			echo "$(YELLOW)Please install Java 17 or above: https://adoptium.net/$(NC)"; \
			exit 1; \
		fi; \
	else \
		echo "$(RED)❌ Java not found$(NC)"; \
		echo "$(YELLOW)Please install Java 17 or above: https://adoptium.net/$(NC)"; \
		exit 1; \
	fi
	@echo ""
	@echo "$(YELLOW)Checking Maven...$(NC)"
	@if command -v mvn >/dev/null 2>&1; then \
		MVN_VERSION=$$(mvn -version 2>/dev/null | head -n 1); \
		echo "$(GREEN)✅ Maven found: $$MVN_VERSION$(NC)"; \
	elif [ -f "./mvnw" ]; then \
		echo "$(GREEN)✅ Maven Wrapper found: ./mvnw$(NC)"; \
	else \
		echo "$(RED)❌ Maven not found and no Maven Wrapper$(NC)"; \
		echo "$(YELLOW)Please install Maven: https://maven.apache.org/install.html$(NC)"; \
		exit 1; \
	fi
	@echo ""
	@echo "$(YELLOW)Checking Docker...$(NC)"
	@if command -v docker >/dev/null 2>&1; then \
		if docker info >/dev/null 2>&1; then \
			DOCKER_VERSION=$$(docker --version); \
			echo "$(GREEN)✅ Docker found and running: $$DOCKER_VERSION$(NC)"; \
		else \
			echo "$(YELLOW)⚠️  Docker found but not running$(NC)"; \
			echo "$(YELLOW)Please start Docker Desktop or Docker service$(NC)"; \
			exit 1; \
		fi; \
	else \
		echo "$(RED)❌ Docker not found$(NC)"; \
		echo "$(YELLOW)Please install Docker: https://docs.docker.com/get-docker/$(NC)"; \
		exit 1; \
	fi
	@echo ""
	@echo "$(GREEN)🎉 All prerequisites satisfied!$(NC)"
	@echo ""

install:
	@echo "$(BLUE)📦 Installing Maven dependencies...$(NC)"
	@./mvnw dependency:resolve
	@echo "$(GREEN)✅ Dependencies installed successfully$(NC)"

clean:
	@echo "Cleaning build artifacts..."
	./mvnw clean

build:
	@echo "Building application..."
	./mvnw clean compile

package:
	@echo "Creating JAR package..."
	./mvnw clean package -DskipTests

test:
	@echo "Running unit tests..."
	./mvnw test

integration:
	@echo "Running integration tests..."
	./mvnw verify

run:
	@echo "Starting Customer Registration System..."
	./mvnw spring-boot:run

dev:
	@echo "Starting in development mode..."
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

docker-up:
	@echo "$(BLUE)🐳 Starting all services with Docker Compose...$(NC)"
	@docker-compose -f src/main/docker/services.yml up -d
	@echo "$(YELLOW)⏳ Waiting for services to be ready...$(NC)"
	@sleep 8
	@echo "$(GREEN)✅ Services are running:$(NC)"
	@echo "  📊 PostgreSQL: localhost:5432"
	@echo "  🔍 Elasticsearch: localhost:9200"
	@echo "  📧 MailHog: localhost:8025"

docker-down:
	@echo "$(BLUE)🛑 Stopping all Docker Compose services...$(NC)"
	@docker-compose -f src/main/docker/services.yml down
	@echo "$(GREEN)✅ All services stopped$(NC)"

logs:
	@echo "Showing application logs..."
	tail -f target/spring.log 2>/dev/null || echo "No log file found. Run 'make run' first."

stop:
	@echo "Stopping application..."
	pkill -f "customer-registration" || echo "No running application found"

status:
	@echo "Checking application status..."
	@if pgrep -f "customer-registration" > /dev/null; then \
		echo "✅ Application is running"; \
		echo "🌐 Available at: http://localhost:8080"; \
		echo "📊 Health check: http://localhost:8080/management/health"; \
	else \
		echo "❌ Application is not running"; \
	fi

start: install build docker-up
	@echo "Quick start complete! Starting application..."
	@sleep 3
	@make run

setup: check-prereqs clean install docker-up build
	@echo ""
	@echo "$(GREEN)🎉 FULL SETUP COMPLETE!$(NC)"
	@echo ""
	@echo "$(BLUE)Services are running:$(NC)"
	@echo "  📊 PostgreSQL: localhost:5432"
	@echo "  🔍 Elasticsearch: localhost:9200"
	@echo "  📧 MailHog: localhost:8025"
	@echo ""
	@echo "$(BLUE)Next steps:$(NC)"
	@echo "  make run    - Start the application"
	@echo "  make dev    - Start in development mode"
	@echo "  make test   - Run tests"
	@echo ""
	@echo "$(YELLOW)To start the app: make run$(NC)"

start-app: docker-up install
	@echo "$(GREEN)🚀 Starting Customer Registration System...$(NC)"
	@echo ""
	@./mvnw spring-boot:run

info:
	@echo "$(BLUE)🔍 Environment Information:$(NC)"
	@echo ""
	@echo "$(YELLOW)Java Version:$(NC)"
	@java -version 2>&1 | head -n 1 || echo "Java not found"
	@echo ""
	@echo "$(YELLOW)Maven Version:$(NC)"
	@./mvnw -version 2>/dev/null | head -n 1 || echo "Maven wrapper not found"
	@echo ""
	@echo "$(YELLOW)Docker Version:$(NC)"
	@docker --version 2>/dev/null || echo "Docker not found"
	@echo ""
	@echo "$(YELLOW)Running Services:$(NC)"
	@docker-compose -f src/main/docker/services.yml ps 2>/dev/null || echo "No services running"
