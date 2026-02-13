# JavaCup Backend

This is the backend service for the JavaCup project, built with Spring Boot 3.x and Java 21.

## Architecture Overview

The backend follows a layered architecture pattern with clear separation of concerns:

### 1. **Presentation Layer** (Controllers)
- **Location**: `src/main/java/com/javacup/backend/controller/`
- **Purpose**: Handles HTTP requests and responses
- **Current Controllers**:
  - `HealthController`: Provides health check and application info endpoints
  - `MatchController`: Runs matches and provides match-related endpoints
  - `TacticsController`: Provides endpoints to list available tactics

### 2. **Service Layer**
- **Location**: `src/main/java/com/javacup/backend/service/`
- **Purpose**: Contains business logic and orchestrates data flow between controllers and repositories
- **Current Services**:
  - `TacticService`: Dynamically discovers and manages tactics information from the `com.javacup.tactics` package

### 3. **Repository Layer** (Future)
- **Location**: `src/main/java/com/javacup/backend/repository/`
- **Purpose**: Handles data persistence and database operations
- **Status**: To be implemented when database integration is needed

### 4. **Domain Layer - Match Engine**
- **Location**: `src/main/java/com/javacup/model/`
- **Purpose**: Core football match simulation engine
- **Status**: ✅ 95% Complete (20/21 files migrated)
  - **Packages**:
    - `model/` - Core interfaces (Tactic, TacticDetail, PlayerDetail)
    - `model/command/` - Player command system (movement, kicking)
    - `model/trajectory/` - Ball physics (aerial, ground trajectories)
    - `model/util/` - Utilities (Constants, Position, validation)
    - `model/engine/` - Match simulation engine (95% complete)
  - **Remaining**:
    - GameSituations.java - Game state provider for tactics
    - Match.java - Main match engine (~1600 lines)
    - SavedMatch.java - Match replay/serialization

## Technology Stack

- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.10
- **Build Tool**: Maven 3.x
- **Testing Framework**: JUnit 5 (Jupiter)
- **Web Framework**: Spring MVC
- **Code Generation**: Lombok (for reducing boilerplate)
- **Logging**: SLF4J + Logback (via Spring Boot)

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/javacup/backend/
│   │   │   │   ├── BackendApplication.java    # Main Spring Boot application
│   │   │   │   └── controller/
│   │   │   │       └── HealthController.java  # REST endpoints
│   │   │   └── com/javacup/model/             # ✨ Match Engine (NEW)
│   │   │       ├── Tactic.java                # AI interface for tactics
│   │   │       ├── TacticDetail.java          # Team configuration
│   │   │       ├── PlayerDetail.java          # Player attributes
│   │   │       ├── UniformStyle.java          # Jersey patterns
│   │   │       ├── command/                   # Player commands
│   │   │       │   ├── Command.java           # Base command class
│   │   │       │   ├── CommandMoveTo.java     # Movement commands
│   │   │       │   └── CommandHitBall.java    # Kicking commands
│   │   │       ├── trajectory/                # Ball physics
│   │   │       │   ├── AbstractTrajectory.java # Trajectory base
│   │   │       │   ├── AirTrajectory.java      # Aerial physics
│   │   │       │   └── FloorTrajectory.java    # Ground roll physics
│   │   │       ├── util/                      # Utilities
│   │   │       │   ├── Constants.java         # Game constants
│   │   │       │   ├── Position.java          # 2D coordinates
│   │   │       │   └── TacticValidate.java    # Validation
│   │   │       └── engine/                    # Match engine
│   │   │           ├── Acceleration.java      # Turn physics
│   │   │           ├── PlayerImpl.java        # Player implementation
│   │   │           ├── TacticImpl.java        # Tactic wrapper
│   │   │           ├── TacticDetailImpl.java  # Config wrapper
│   │   │           ├── Iteration.java         # Replay data
│   │   │           └── MatchInterface.java    # Public API
│   │   │           # TODO: GameSituations, Match, SavedMatch
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

### Using Docker

Build the Docker image:

```bash
docker build -t javacup-backend .
```

Run the container:

```bash
docker run -p 8080:8080 javacup-backend
```

The application will start on port 8080 by default.

## Docker Support

The backend includes a multi-stage Dockerfile optimized for production:

- **Stage 1 (Builder)**: Uses `maven:3.9-eclipse-temurin-21` to build the application
  - Leverages Docker layer caching for dependencies
  - Compiles and packages the application
  
- **Stage 2 (Runtime)**: Uses `eclipse-temurin:21-jre-jammy` for a smaller runtime image
  - Only includes the JAR file and JRE (no build tools)
  - Includes health check support
  - Exposes port 8080

### Docker Commands

Build the image:
```bash
docker build -t javacup-backend .
```

Run the container:
```bash
docker run -d -p 8080:8080 --name javacup-backend javacup-backend
```

View logs:
```bash
docker logs -f javacup-backend
```

Stop the container:
```bash
docker stop javacup-backend
```

Remove the container:
```bash
docker rm javacup-backend
```

### Using with Docker Compose

See the root README.md for information about running the entire stack (backend + frontend) with Docker Compose.

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

### Match Endpoints

#### Get Available Tactics
- **URL**: `GET /api/match/tactics`
- **Description**: Returns a list of available tactics that can be used in matches
- **Response**:
  ```json
  {
    "tactics": ["SimpleTactic", "DefaultHome", "DefaultAway"],
    "count": 3
  }
  ```

#### Run a Match
- **URL**: `POST /api/match/run`
- **Description**: Runs a complete match (60 seconds, 3600 iterations) between two tactics and returns a complete SavedMatch object with all match data
- **Request Body**:
  ```json
  {
    "homeTacticName": "SimpleTactic",
    "awayTacticName": "DefaultHome"
  }
  ```
- **Success Response** (200 OK):
  Returns a SavedMatch object with complete match data:
  ```json
  {
    "totalIterations": 3600,
    "homeTeam": {
      "teamName": "SimpleTactic",
      "country": "Test Country",
      "coach": "Test Coach",
      "players": [...]
    },
    "awayTeam": {
      "teamName": "DefaultHome",
      "country": "Test Country",
      "coach": "Test Coach",
      "players": [...]
    },
    "finalHomeGoals": 0,
    "finalAwayGoals": 0,
    "finalHomePossession": 0.48,
    "iterations": [
      {
        "iteration": 1,
        "homeGoals": 0,
        "awayGoals": 0,
        "ballHeight": 0,
        "visibleBallX": 0,
        "visibleBallY": 0,
        "positions": [...],
        "events": {...}
      },
      ...
    ]
  }
  ```
  The response includes:
  - Team configurations (names, players, etc.)
  - Final score and possession statistics
  - All 3600 iterations with player positions, ball state, and events for replay purposes
- **Error Response** (404 Not Found):
  ```json
  {
    "error": "Tactic(s) not found: NonExistentTactic",
    "missingTactics": ["NonExistentTactic"],
    "availableTactics": ["SimpleTactic", "DefaultHome", "DefaultAway"]
  }
  ```
- **Notes**:
  - No authentication required
  - Match runs for full 60 seconds (3600 iterations at 60 iterations/second)
  - Returns comprehensive match statistics including score, possession, and final positions
  - If one or both tactics don't exist, returns HTTP 404 with helpful error message
### List Tactics
- **URL**: `GET /api/tactics`
- **Description**: Returns a list of all available tactics (team AI strategies) that can be selected for a match. The list is dynamically discovered by scanning the `com.javacup.tactics` package for implementations of the `Tactic` interface. Each subdirectory under the tactics package represents a unique tactic. No authentication required.
- **Response**:
  ```json
  {
    "tactics": [
      "masia13",
      "pistachos",
      "romedal",
      "twentythree"
    ],
    "count": 4
  }
  ```
  
  **Note**: The tactics list is dynamically generated. As new tactics are added to the `com.javacup.tactics` package, they will automatically appear in the response.

## Configuration

The application can be configured through `src/main/resources/application.properties`:

- `server.port`: The port on which the application runs (default: 8080)
- `spring.application.name`: The name of the application
- `application.version`: Application version (from pom.xml via Maven filtering)
- `application.name`: Application name (from pom.xml via Maven filtering)
- `application.description`: Application description (from pom.xml via Maven filtering)

## Match Engine Architecture

The match engine (`com.javacup.model`) simulates football matches with realistic physics:

### Core Components

1. **Tactics & Players**
   - `Tactic`: AI interface for team control (execute commands every iteration)
   - `TacticDetail`: Team configuration (name, uniforms, players)
   - `PlayerDetail`: Individual player attributes (speed, power, precision)
   
2. **Command System**
   - `CommandMoveTo`: Direct player movement (with optional sprint)
   - `CommandHitBall`: Ball kicking (supports angle/coordinate targeting, power control)

3. **Physics Engine**
   - **Ball Trajectories**: 
     - `AirTrajectory`: Aerial physics with gravity & air resistance
     - `FloorTrajectory`: Ground roll with friction
     - Automatic bouncing and trajectory chaining
   - **Player Movement**:
     - `Acceleration`: Momentum-based turning (can't instantly change direction)
     - Energy system (sprint costs energy, passive recovery)
     - Speed affected by attributes, energy, and acceleration

4. **Match Engine** (⚠️ Partially Complete)
   - `MatchInterface`: Public API for match execution
   - `GameSituations`: Provides game state to tactics (TODO)
   - `Match`: Main simulation engine - 60 iterations/second (TODO)
   - `SavedMatch`: Match recording for replays (TODO)

### Match Simulation Flow

```
Every Iteration (1/60th of a second):
1. Update ball physics (trajectory)
2. Call both tactics → get commands
3. Process movements (with physics)
4. Process kicks (one per iteration max)
5. Detect events (goals, out of bounds, fouls)
6. Update energy & acceleration
7. Store state if recording
```

### Key Features

- **Realistic Physics**: Gravity, air resistance, friction, bouncing
- **Energy Management**: Sprint wisely or run out of stamina
- **Turn Penalties**: Can't change direction at full speed
- **Credit System**: Limited points to distribute across player attributes
- **Validation**: Automatic checks for rules compliance
- **Replay System**: Save and replay matches

### Migration Status

✅ **Completed (95%)**:
- All interfaces and data structures
- Command system (movement, kicking)
- Physics engine (trajectories, acceleration)
- Utility classes (position, constants, validation)
- Wrapper classes (tactic implementations, iteration storage)

🔄 **In Progress (5%)**:
- GameSituations (~400 lines): Game state provider for tactics
- Match (~1600 lines): Core match engine  
- SavedMatch (~500 lines): Match serialization for replays

### For Developers

To create a tactic:
1. Implement `Tactic` interface
2. Return `TacticDetail` with team configuration
3. Implement `execute()` to return commands each iteration
4. Implement positioning methods for kickoffs

See `javacup2013/BACKEND.md` for detailed architecture documentation.

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
