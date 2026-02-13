# javacup2
Based on javacup from javahispano, a football matches experience for programmers, or not?

## Project Structure

This project consists of two main components:

- **Backend**: Spring Boot 3.x application (Java 21) - REST API for match simulation
- **Frontend**: React 19 + Three.js application - 3D visualization interface

## Quick Start with Docker

The easiest way to run the entire JavaCup2 stack is using Docker:

### Prerequisites

- Docker or Podman installed on your system
- Docker Compose (or podman-compose)

### Running the Stack

#### Linux/macOS

```bash
# Make the script executable (first time only)
chmod +x run-stack.sh

# Run interactively
./run-stack.sh

# Or run with options
./run-stack.sh --runtime docker --command up
```

#### Windows

```cmd
REM Run interactively
run-stack.bat

REM Or run with options
run-stack.bat --runtime docker --command up
```

### Available Commands

The run scripts support the following commands:

- **up**: Start the stack (builds images if needed)
- **down**: Stop and remove the stack
- **restart**: Restart all services
- **logs**: View logs from all services
- **ps**: Show running containers
- **build**: Rebuild images without cache

### Accessing the Application

Once the stack is running:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Health Check**: http://localhost:8080/api/health

## Docker Architecture

### Backend Service

- **Base Image**: Eclipse Temurin 21 JRE
- **Build Tool**: Maven (multi-stage build)
- **Port**: 8080
- **Health Check**: Monitors application health via actuator endpoint

### Frontend Service

- **Base Image**: Nginx Alpine
- **Build Tool**: Vite (multi-stage build)
- **Port**: 3000 (mapped to container port 80)
- **Health Check**: Monitors nginx availability

### Network

Both services run on a shared bridge network (`javacup-network`) allowing them to communicate.

## Manual Setup (Without Docker)

### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

See [backend/README.md](backend/README.md) for detailed instructions.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

See [frontend/README.md](frontend/README.md) for detailed instructions.

## Docker Compose Configuration

The `docker-compose.yml` file orchestrates both services:

```yaml
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    
  frontend:
    build: ./frontend
    ports:
      - "3000:80"
    depends_on:
      backend:
        condition: service_healthy
```

### Runtime Selection

The run scripts support both Docker and Podman:

- Automatically detects available runtime
- Can be explicitly specified with `--runtime docker` or `--runtime podman`
- Uses appropriate compose command (docker-compose, docker compose, or podman-compose)

## Development

For development, you may prefer running services individually to take advantage of hot-reloading:

**Backend** (with auto-reload):
```bash
cd backend
mvn spring-boot:run
```

**Frontend** (with hot module replacement):
```bash
cd frontend
npm run dev
```

## Production Deployment

The Docker images are production-ready:

- Multi-stage builds minimize image size
- Health checks ensure service availability
- Optimized for security and performance
- No development dependencies in final images

To deploy:

1. Build images: `./run-stack.sh --command build`
2. Tag images for your registry
3. Push to container registry
4. Deploy to your container orchestration platform

## Troubleshooting

### Port Conflicts

If ports 3000 or 8080 are already in use:

1. Edit `docker-compose.yml` to change the port mappings
2. For example: `"3001:80"` instead of `"3000:80"`

### Container Not Starting

Check logs:
```bash
./run-stack.sh --command logs
```

Or for a specific service:
```bash
docker logs javacup-backend
docker logs javacup-frontend
```

### Rebuilding Images

If you make changes to the code:
```bash
./run-stack.sh --command build
./run-stack.sh --command up
```

## Contributing

When contributing to this project:

1. Make changes to backend or frontend
2. Test locally with Docker: `./run-stack.sh --command build && ./run-stack.sh --command up`
3. Ensure all services start successfully
4. Submit a pull request

## License

This project is open source and available under the MIT License.
