# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an education management system built as a **monorepo** containing:
- **Backend**: Spring Modulith application using Spring Boot 3.5.3 and Java 21
- **Frontend**: React 19 with TypeScript, Vite, and Material-UI

The backend follows a modular monolith architecture with clear domain boundaries and event-driven communication between modules.

## Project Structure

```
edu-manager/
├── backend/                # Spring Boot backend application
│   ├── src/main/java/
│   │   └── com/edumanager/
│   │       ├── application/    # Main app & configurations
│   │       ├── shared/         # Shared module (security, DTOs, exceptions)
│   │       ├── user/           # User domain module
│   │       └── student/        # Student domain module
│   └── src/main/resources/
│       ├── application.yml     # Main configuration
│       └── keys/              # RSA keys for JWT
├── frontend/              # React TypeScript frontend
│   ├── src/
│   │   ├── components/    # React components
│   │   ├── services/      # API service layer
│   │   └── types/         # TypeScript types
│   └── package.json
├── docker-compose.yml     # Infrastructure services
├── scripts/              # Database init scripts
└── package.json          # Monorepo root (uses pnpm workspaces)
```

## Development Commands

### Infrastructure
```bash
# Start all services (PostgreSQL, MongoDB, Redis, Kafka)
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f [service-name]
```

### Backend Development
```bash
# Navigate to backend
cd backend

# Build the application
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Run specific test
./gradlew test --tests "com.edumanager.BackendApplicationTests"

# Clean build
./gradlew clean build

# Run with development profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Generate dependency report
./gradlew dependencies
```

### Frontend Development
```bash
# From root directory (uses pnpm workspace)
pnpm dev        # Start dev server
pnpm build      # Build for production
pnpm lint       # Run ESLint
pnpm type-check # TypeScript type checking

# Or navigate to frontend directory
cd frontend
npm run dev     # Alternative if using npm
```

## Architecture

### Spring Modulith Structure
The application uses Spring Modulith to enforce module boundaries and enable event-driven communication:

- **Domain Modules**: Each business domain (`user`, `student`) is a separate module with its own package structure
- **Shared Module**: Common functionality, exceptions, security, and cross-cutting concerns
- **Application Module**: Main application class and configuration

### Module Boundaries
- Modules communicate via **events** rather than direct dependencies
- Each module can have its own database schema/collection
- Modules are verified at startup for architectural compliance

### Technology Stack

#### Backend
- **Framework**: Spring Boot 3.5.3, Spring Modulith 1.4.1
- **Language**: Java 21 (with Virtual Threads enabled)
- **Databases**: 
  - PostgreSQL (primary JPA entities)
  - MongoDB (document storage, Modulith event store)
  - Redis (caching/sessions)
- **Messaging**: Apache Kafka with Spring Kafka
- **Security**: Spring Security with OAuth2 Resource Server (RSA JWT)
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger UI)
- **Testing**: JUnit 5, Spring Boot Test, Spring Modulith Test, Testcontainers

#### Frontend
- **Framework**: React 19
- **Language**: TypeScript 5.8
- **Build Tool**: Vite 7
- **UI Library**: Material-UI (MUI) v7
- **HTTP Client**: Axios
- **Package Manager**: pnpm (workspace enabled)
- **Linting**: ESLint 9 with TypeScript support
- **Formatting**: Prettier

## Database Configuration

The application supports multiple databases:

1. **PostgreSQL**: Primary relational database for core entities
2. **MongoDB**: Document storage for flexible/schema-less data
3. **Redis**: Caching and session management

Database connections are configured through `application.yml` with environment-specific profiles.

## Event-Driven Architecture

- **Internal Events**: Spring Modulith events for inter-module communication
- **External Events**: Kafka for external system integration
- **Event Store**: Modulith can persist events for replay and auditing

## Testing Strategy

### Test Types
- **Unit Tests**: Standard JUnit 5 tests for individual components
- **Integration Tests**: Spring Boot Test with test slices
- **Modulith Tests**: Verify module structure and event publishing/handling
- **Security Tests**: Spring Security Test for authentication/authorization

### Running Tests
```bash
# All tests
./gradlew test

# Specific test class
./gradlew test --tests "BackendApplicationTests"

# Tests with specific profile
./gradlew test -Dspring.profiles.active=test
```

## Module Development Guidelines

### Adding New Domain Modules
1. Create package under `com.edumanager.[domain]`
2. Define clear module boundaries (package-info.java)
3. Use events for inter-module communication
4. Follow naming conventions: `[Domain]Service`, `[Domain]Repository`, etc.

### Shared Components
- **Exceptions**: Common business exceptions in `shared` module
- **Security**: Authentication/authorization components
- **Events**: Shared event definitions for inter-module communication
- **DTOs**: Response wrappers and common data structures

### Security Implementation
- OAuth2 Resource Server configuration
- JWT token validation
- Role-based access control
- Custom security configurations per module if needed

## Recent Development Progress

### ✅ Completed Features
1. **JWT Security Implementation** (Spring Security 6 + OAuth2 Resource Server)
   - RSA-based JWT authentication with access/refresh tokens
   - JwtService with token generation and validation
   - JwtAuthenticationFilter for request processing
   - Custom JWT exception handling (AuthenticationEntryPoint, AccessDeniedHandler)
   - 4-tier role system: ADMIN > INSTRUCTOR > PARENT > STUDENT

2. **Configuration Management**
   - Type-safe @ConfigurationProperties (JwtProperties)
   - Centralized constants (SecurityConstants)
   - Environment-specific profiles (local, dev, prod)
   - Custom Duration validation for JWT expiration

3. **Infrastructure Setup**
   - Docker Compose with PostgreSQL, Redis, Kafka, MongoDB
   - Spring Modulith event configuration (MongoDB storage)
   - Port conflict resolution (Spring Boot: 8081, Kafka UI: 8082)
   - Bean conflict resolution (TransactionManager, EventRepository)

4. **Error Handling System**
   - Comprehensive ErrorCode enum with Java 21 Pattern Matching
   - Global exception handler with structured responses
   - HTTP status code mapping and error categorization

### 🚧 Current Tasks
- **User Entity**: Create with JPA validation annotations
- **Authentication Controller**: Login/register endpoints
- **User Service & Repository**: Business logic and data access

### 📋 Pending Features
- JWT token blacklist service (Redis-based)
- Course module implementation
- Student enrollment system
- Parent-child relationship management

## Key Configuration Details

### Ports
- **Backend API**: 8081 (context path: /api)
- **PostgreSQL**: 5432
- **MongoDB**: 27017
- **Redis**: 6379
- **Kafka**: 29092
- **Kafka UI**: 8082
- **Frontend Dev Server**: 5173 (Vite default)

### JWT Configuration
- **Algorithm**: RSA-256 with public/private key pairs
- **Token Types**: Access token (24h) and Refresh token (7d)
- **Key Location**: `backend/src/main/resources/keys/`
- **Key Generation**: Run `generate-keys.sh` before first start

### Environment Profiles
- **local**: Development with verbose logging
- **dev**: Development environment settings
- **prod**: Production with minimal logging, DDL validation only

## Development Notes

- **Monorepo**: Uses pnpm workspaces for managing frontend dependencies
- **Virtual Threads**: Enabled for improved concurrency (Java 21 feature)
- **Bean Overrides**: Allowed to handle multi-database transaction manager conflicts
- **MongoDB Events**: Enabled for Spring Modulith event persistence
- **CORS**: Configured for localhost:3000 and localhost:5173 in local profile
- **API Base URL**: All backend endpoints are prefixed with `/api`
- **Docker Compose**: Set to manual start (`docker-compose up -d` required)

## Common Development Tasks

### Generate JWT Keys (First Time Setup)
```bash
cd backend
./generate-keys.sh  # Unix/Mac
# or
generate-keys.bat   # Windows
```

### Access Services
- **API Documentation**: http://localhost:8081/api/swagger-ui.html
- **Kafka UI**: http://localhost:8082
- **Frontend**: http://localhost:5173

### Database Migrations
- Development: Hibernate `ddl-auto: update`
- Production: Hibernate `ddl-auto: validate` (use Flyway/Liquibase for migrations)

### Module Communication
- Use Spring Modulith events for inter-module communication
- Avoid direct dependencies between domain modules
- Shared module contains common utilities and cross-cutting concerns

## Code Style and Conventions

### Backend (Java)
- **Naming Conventions**:
  - Classes: PascalCase (e.g., `UserService`, `StudentRepository`)
  - Methods/Variables: camelCase (e.g., `findByEmail`, `userId`)
  - Constants: UPPER_SNAKE_CASE (e.g., `MAX_LOGIN_ATTEMPTS`)
  - Packages: lowercase (e.g., `com.edumanager.user.service`)

- **Spring Annotations**:
  - Use constructor injection over field injection
  - Prefer `@RequiredArgsConstructor` with `final` fields
  - Use `@RestController` for API endpoints
  - Apply `@Transactional` at service layer

- **API Design**:
  - RESTful endpoints: `/api/v1/resource`
  - Use proper HTTP methods (GET, POST, PUT, DELETE)
  - Return consistent response format using `ApiResponse<T>`
  - Handle exceptions globally with `@ControllerAdvice`

### Frontend (React/TypeScript)
- **Component Structure**:
  - Functional components with hooks
  - TypeScript interfaces for props
  - Separate business logic into custom hooks
  - Keep components focused and single-purpose

- **File Naming**:
  - Components: PascalCase (e.g., `UserProfile.tsx`)
  - Utilities: camelCase (e.g., `formatDate.ts`)
  - Types: PascalCase with `.types.ts` suffix
  - Tests: `*.test.tsx` or `*.spec.tsx`

- **State Management**:
  - Use React Context for global state
  - Local state with `useState` and `useReducer`
  - Server state with React Query (if added)

## Security Best Practices

1. **Authentication**:
   - All endpoints except `/api/v1/auth/*` require JWT token
   - Tokens sent via `Authorization: Bearer <token>` header
   - Refresh tokens stored securely (httpOnly cookies recommended)

2. **Authorization**:
   - Role hierarchy: ADMIN > INSTRUCTOR > PARENT > STUDENT
   - Use `@PreAuthorize` for method-level security
   - Implement row-level security for data access

3. **Data Validation**:
   - Use Bean Validation annotations
   - Validate at controller and service layers
   - Sanitize user input to prevent XSS/SQL injection

## Troubleshooting

### Common Issues

1. **JWT Keys Not Found**:
   ```
   Error: Cannot find private.pem or public.pem
   Solution: Run ./generate-keys.sh in backend directory
   ```

2. **Port Already in Use**:
   ```
   Error: Port 8081 is already in use
   Solution: Change server.port in application.yml or stop conflicting service
   ```

3. **Docker Services Not Starting**:
   ```
   Error: Cannot connect to PostgreSQL/Redis/Kafka
   Solution: Run 'docker-compose up -d' and check 'docker-compose ps'
   ```

4. **Bean Definition Override Error**:
   ```
   Error: Bean 'transactionManager' already defined
   Solution: Ensure spring.main.allow-bean-definition-overriding=true in application.yml
   ```

5. **Frontend CORS Issues**:
   ```
   Error: CORS policy blocks request
   Solution: Check app.cors.allowed-origins includes your frontend URL
   ```

### Useful Commands for Debugging

```bash
# Check application logs
tail -f backend/logs/edu-manager.log

# Check Docker container logs
docker-compose logs -f postgres
docker-compose logs -f kafka

# Test database connection
docker exec -it edu-manager-postgres psql -U edumanager -d edumanager

# Check Kafka topics
docker exec -it edu-manager-kafka kafka-topics --list --bootstrap-server localhost:29092

# Clear Redis cache
docker exec -it edu-manager-redis redis-cli FLUSHALL
```

## Contributing Guidelines

1. **Branch Naming**: `feature/description`, `fix/description`, `docs/description`
2. **Commit Messages**: Use conventional commits (feat:, fix:, docs:, etc.)
3. **Pull Requests**: Include description of changes and testing performed
4. **Code Review**: All PRs require at least one review before merging
5. **Testing**: Maintain test coverage above 80% for new code