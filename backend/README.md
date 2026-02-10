# JavaCup Backend

This is the backend service for the JavaCup project, built with Spring Boot 3.x and Java 21.

## Architecture Overview

The backend follows a layered architecture pattern with clear separation of concerns:

### 1. **Presentation Layer** (Controllers)
- **Location**: `src/main/java/com/javacup/backend/controller/`
- **Purpose**: Handles HTTP requests and responses
- **Current Controllers**:
  - `HealthController`: Provides health check and application info endpoints

### 2. **Service Layer** (Future)
- **Location**: `src/main/java/com/javacup/backend/service/`
- **Purpose**: Contains business logic and orchestrates data flow between controllers and repositories
- **Status**: To be implemented as business requirements evolve

### 3. **Repository Layer** (Future)
- **Location**: `src/main/java/com/javacup/backend/repository/`
- **Purpose**: Handles data persistence and database operations
- **Status**: To be implemented when database integration is needed

### 4. **Domain Layer** (Future)
- **Location**: `src/main/java/com/javacup/backend/model/` or `domain/`
- **Purpose**: Contains entity classes and domain models
- **Status**: To be implemented as the data model is defined

## Technology Stack

- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.10
- **Build Tool**: Maven 3.x
- **Testing Framework**: JUnit 5 (Jupiter)
- **Web Framework**: Spring MVC

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/javacup/backend/
│   │   │       ├── BackendApplication.java    # Main Spring Boot application
│   │   │       └── controller/
│   │   │           └── HealthController.java  # REST endpoints
│   │   └── resources/
│   │       └── application.properties         # Application configuration
│   └── test/
│       └── java/
│           └── com/javacup/backend/
│               ├── BackendApplicationTest.java       # Context loading test
│               └── controller/
│                   └── HealthControllerTest.java     # Controller unit tests
├── pom.xml                                    # Maven configuration
├── .gitignore                                 # Git ignore rules
└── README.md                                  # This file
```

## Prerequisites

- Java 21 (JDK 21) installed
- Maven 3.6+ installed
- Git installed

## Building the Project

To compile the project:

```bash
mvn clean compile
```

## Running Tests

To run all tests:

```bash
mvn test
```

To run tests with verification (includes integration tests):

```bash
mvn clean verify
```

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Java directly

```bash
mvn clean package
java -jar target/backend-2.0.0.jar
```

The application will start on port 8080 by default.

## API Endpoints

### Health Check
- **URL**: `GET /api/health`
- **Description**: Returns the health status of the application
- **Response**:
  ```json
  {
    "status": "UP",
    "message": "JavaCup Backend is running"
  }
  ```

### Application Info
- **URL**: `GET /api/info`
- **Description**: Returns information about the application
- **Response**:
  ```json
  {
    "application": "JavaCup Backend",
    "version": "2.0.0",
    "description": "Backend service for JavaCup project"
  }
  ```

## Configuration

The application can be configured through `src/main/resources/application.properties`:

- `server.port`: The port on which the application runs (default: 8080)
- `spring.application.name`: The name of the application
- `application.version`: Application version (from pom.xml via Maven filtering)
- `application.name`: Application name (from pom.xml via Maven filtering)
- `application.description`: Application description (from pom.xml via Maven filtering)

## Design Patterns and Best Practices

### Current Implementation
1. **RESTful API Design**: Following REST principles for API endpoints
2. **Dependency Injection**: Using Spring's IoC container for managing dependencies
3. **Separation of Concerns**: Controllers handle HTTP, business logic will be in services
4. **Test-Driven Development**: Unit tests for all controller endpoints

### Future Enhancements
1. **Service Layer**: Add business logic layer as requirements grow
2. **Data Persistence**: Integrate with a database (JPA/Hibernate)
3. **Security**: Implement authentication and authorization (Spring Security)
4. **API Documentation**: Add Swagger/OpenAPI for API documentation
5. **Exception Handling**: Global exception handler for consistent error responses
6. **Logging**: Structured logging with SLF4J/Logback
7. **Validation**: Input validation using Bean Validation (JSR-380)
8. **DTOs**: Data Transfer Objects for API requests/responses

## CI/CD

The project includes a GitHub Actions workflow (`.github/workflows/backend-ci.yml`) that:
- Builds the project on every push and pull request
- Runs all tests
- Verifies the build with `mvn clean verify`

## Development Workflow

1. Create a new branch for your feature/fix
2. Write tests for new functionality (TDD approach)
3. Implement the feature/fix
4. Run tests locally: `mvn test`
5. Verify the build: `mvn clean verify`
6. Commit and push your changes
7. Create a pull request

## Troubleshooting

### Port Already in Use
If you get an error that port 8080 is already in use, you can:
1. Change the port in `application.properties`: `server.port=8081`
2. Or stop the process using port 8080

### Maven Build Fails
1. Ensure you have Java 21 installed: `java -version`
2. Ensure you have Maven installed: `mvn -version`
3. Clear Maven cache: `mvn clean`
4. Delete `target/` directory and rebuild

## Contributing

When contributing to the backend:
1. Follow the existing code structure and patterns
2. Write unit tests for all new functionality
3. Ensure all tests pass before submitting a PR
4. Update this README if you add new features or change architecture

## License

This project is licensed under the same license as the parent JavaCup2 project.
