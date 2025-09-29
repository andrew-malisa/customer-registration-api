# Customer Registration System - Mobile Agent API

A robust Spring Boot microservice for managing customer registrations and agent operations in telecommunications environments, designed for mobile agent applications.

## 📋 Assessment Summary

This project addresses a scenario-based assessment for a mobile agent application with the following requirements:

### ✅ Core Features Implemented

**(a) Agent Authentication** 
- **Endpoint**: `POST /api/authenticate`
- **Implementation**: Username/password authentication with JWT tokens
- **Current Status**: ✅ Fully implemented (uses phone number)

**(b) Agent Profile Information**
- **Endpoint**: `GET /api/v1/agents/{id}/details` 
- **Returns**: Complete agent profile with location (region, district, ward), status, and user details
- **Current Status**: ✅ Fully implemented

**(c) Customer Registration**
- **Endpoint**: `POST /api/v1/customers`
- **Features**: Full customer details with NIDA validation, location tracking, audit logging
- **Current Status**: ✅ Fully implemented with comprehensive validation

**(d) Agent Activity History**
- **Endpoint**: `GET /api/v1/activity-logs/me`
- **Features**: Paginated, searchable activity history with filtering by action type
- **Current Status**: ✅ Fully implemented with advanced search capabilities

**(e) Agent Logout**
- **Endpoint**: `POST /api/logout`
- **Features**: Secure token invalidation with activity logging
- **Current Status**: ✅ Fully implemented

### 🏗️ Architecture & Design Choices
- **Language**: Java 17
- **Framework**: Spring Boot 3.x with Spring Security
- **Database**: PostgreSQL for reliability and ACID compliance
- **Search**: Elasticsearch for fast, scalable search operations
- **Authentication**: JWT tokens with role-based security
- **API Design**: RESTful with comprehensive OpenAPI documentation

### 🔒 Security Implementation
- BCrypt password encryption (60-character hashes)
- JWT token-based authentication with configurable expiration
- Method-level security annotations (`@PreAuthorize`)
- Input validation and sanitization
- Comprehensive audit logging for all operations
- Role-based access control (ROLE_ADMIN, ROLE_AGENT)

### 🚀 Performance & Scalability Features
- Elasticsearch integration for lightning-fast search
- Database indexing strategy with proper foreign keys
- Connection pooling with HikariCP
- Async processing for non-critical operations
- Stateless design for horizontal scaling
- Automatic database seeding with Elasticsearch reindexing

### 🔄 CI/CD Pipeline Features
- **Build**: Maven-based with dependency management
- **Testing**: Comprehensive unit and integration tests with TestContainers
- **Quality Gates**: Checkstyle enforcement and static analysis
- **Containerization**: Docker support with multi-stage builds
- **Cloud Deployment**: AWS ECR and EC2 integration
- **Infrastructure**: Automated service provisioning

## Overview

This system provides a comprehensive solution for customer onboarding and management, featuring secure authentication, detailed audit logging, and streamlined agent workflows. Built with enterprise-grade patterns and scalability in mind.

## Key Features

### Customer Management
- Secure customer registration with comprehensive validation
- Snowflake-based unique ID generation for distributed systems
- Full CRUD operations with search capabilities
- Email verification and activation workflows

### Agent Operations
- Agent lifecycle management (registration, activation, deactivation)
- Role-based access control with granular permissions
- Performance tracking and status monitoring
- Comprehensive agent profile management

### Security & Audit
- JWT-based authentication with configurable expiration
- Comprehensive audit logging for all operations
- Activity tracking with IP and user agent capture
- Secure password management with reset capabilities

### Technical Stack
- **Framework**: Spring Boot 3.x with Spring Security
- **Database**: PostgreSQL with JPA/Hibernate
- **Search**: Elasticsearch integration for advanced querying
- **Authentication**: JWT tokens with Spring Security
- **Testing**: Comprehensive test suite with TestContainers
- **Build**: Maven with quality gates and static analysis

## Architecture

The application follows a layered architecture pattern:
- **Web Layer**: REST controllers with OpenAPI documentation
- **Service Layer**: Business logic with transaction management
- **Repository Layer**: JPA repositories with custom queries
- **Domain Layer**: Entity models with proper relationships

## Quick Start

### Prerequisites
- Java 17 or higher
- Docker (for databases)
- Maven (optional - we include Maven Wrapper)

### 🚀 Super Easy Setup (First Time)
```bash
# This checks everything, installs dependencies, starts databases, builds the app
make setup

# Then start the application
make run
```

### 🏃‍♂️ Daily Development (After Setup)
```bash
# Quick start for daily work - starts services and runs the app
make start-app
```

### 📋 All Available Commands
```bash
# See all available commands
make help

# Setup & Prerequisites
make setup         # Complete first-time setup
make check-prereqs # Check Java, Maven, Docker are installed

# Development
make start-app     # Quick daily startup (services + app)
make run          # Start the application
make dev          # Start in development mode with hot reload
make build        # Build the application
make test         # Run unit tests
make integration  # Run integration tests

# Database Services (PostgreSQL + Elasticsearch + MailHog)
make docker-up    # Start all database services
make docker-down  # Stop all database services

# Utilities
make clean        # Clean build artifacts
make stop         # Stop running application
make status       # Check if application is running
make logs         # View application logs
make info         # Show environment info
```

### 🔧 Manual Setup (if you prefer)
```bash
# 1. Start the databases
docker-compose -f src/main/docker/services.yml up -d

# 2. Install dependencies
./mvnw dependency:resolve

# 3. Run the application
./mvnw spring-boot:run
```

### 🌐 Access the Application
- **Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/management/health
- **Database**: PostgreSQL on localhost:5432
- **Search**: Elasticsearch on localhost:9200
- **Email Testing**: MailHog on localhost:8025

## Configuration

Key configuration properties:

```yaml
# Application settings
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/customer_registration
    username: app_user
    password: secure_password
  
  # JWT configuration
  security:
    jwt:
      base64-secret: your-base64-encoded-secret
      token-validity-in-seconds: 86400

# Snowflake ID generation
snowflake:
  datacenter-id: 1
  machine-id: 1
```

## API Endpoints

### Authentication
- `POST /api/authenticate` - User login
- `POST /api/account/reset-password/init` - Password reset request
- `POST /api/account/reset-password/finish` - Complete password reset

### Customer Management
- `GET /api/customers` - List customers with pagination
- `POST /api/customers` - Register new customer
- `GET /api/customers/{id}` - Get customer details
- `PUT /api/customers/{id}` - Update customer information
- `DELETE /api/customers/{id}` - Deactivate customer

### Agent Operations
- `GET /api/agents` - List agents with filtering
- `POST /api/agents` - Register new agent
- `GET /api/agents/{id}` - Get agent details
- `PUT /api/agents/{id}/status` - Update agent status
- `GET /api/agents/{id}/activities` - Get agent activity history

### Audit & Monitoring
- `GET /api/activity-logs` - System activity logs
- `GET /api/activity-logs/agent/{agentId}` - Agent-specific activities
- `GET /api/management/health` - Application health status

## Development Guidelines

### Code Quality
- Checkstyle enforced for consistent formatting
- Comprehensive unit and integration test coverage
- SonarQube integration for static analysis
- Prettier configuration for consistent code style

### Database Design
- All entities use Snowflake IDs for distributed scalability
- Comprehensive audit columns on all business entities
- Proper indexing strategy for performance optimization
- Foreign key constraints for data integrity

### Security Considerations
- All passwords are BCrypt hashed
- JWT tokens include role-based claims
- API endpoints protected with method-level security
- Input validation on all user-facing endpoints
- Rate limiting implemented for authentication endpoints

## Monitoring & Operations

### Health Checks
The application exposes standard Spring Boot Actuator endpoints:
- `/management/health` - Application health
- `/management/info` - Application information
- `/management/metrics` - Application metrics

### Logging
Structured logging with correlation IDs for request tracing:
```
2025-09-28 10:30:45.123 INFO [customer-registration-system,trace-id,span-id] CustomerService : Customer registered successfully [customerId=123456789]
```

### Performance Monitoring
- Database query performance tracking
- Service method execution time monitoring
- Memory and CPU usage metrics
- Custom business metrics for registration rates

## Deployment

### Docker Support
```dockerfile
FROM openjdk:17-jre-slim
COPY target/customer-registration-system.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Environment Configuration
- Development: Local PostgreSQL, embedded Elasticsearch
- Staging: Containerized services with Docker Compose
- Production: Kubernetes deployment with external databases

## Contributing

1. Follow the established code style and patterns
2. Ensure all tests pass before submitting PRs
3. Update documentation for any API changes
4. Include integration tests for new features
5. Verify security implications of changes

## License

Internal use only - Proprietary software for telecommunications customer management.
