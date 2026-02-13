# Docker Setup Guide

This document provides comprehensive information about running JavaCup2 with Docker.

## Table of Contents

- [Quick Start](#quick-start)
- [Prerequisites](#prerequisites)
- [Architecture](#architecture)
- [Running the Stack](#running-the-stack)
- [Individual Services](#individual-services)
- [Development Workflow](#development-workflow)
- [Troubleshooting](#troubleshooting)
- [Advanced Configuration](#advanced-configuration)

## Quick Start

The fastest way to get JavaCup2 running:

```bash
# Linux/macOS
./run-stack.sh

# Windows
run-stack.bat
```

Then access:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080

## Prerequisites

### Required
- **Docker** (20.10+) or **Podman** (4.0+)
- **Docker Compose** (2.0+) or **podman-compose** (1.0+)

### Optional
- Git (for cloning the repository)
- 2GB+ of available disk space
- 4GB+ of available RAM (for building images)

### Installation

**Docker Desktop** (Windows/macOS):
- Download from https://www.docker.com/products/docker-desktop

**Docker Engine** (Linux):
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install docker.io docker-compose-plugin

# Fedora
sudo dnf install docker-ce docker-compose-plugin
```

**Podman** (Linux):
```bash
# Ubuntu/Debian
sudo apt-get install podman podman-compose

# Fedora
sudo dnf install podman podman-compose
```

## Architecture

### Backend Container

- **Base Image**: `eclipse-temurin:21-jre-jammy`
- **Build Stage**: `maven:3.9-eclipse-temurin-21`
- **Application**: Spring Boot 3.5.10
- **Port**: 8080
- **Health Check**: `GET /api/health`
- **Startup Time**: ~30-40 seconds

### Frontend Container

- **Base Image**: `nginx:1.27-alpine`
- **Build Stage**: `node:22-alpine`
- **Application**: React 19 + Vite
- **Port**: 3000 (mapped to container port 80)
- **Health Check**: HTTP GET on `/`
- **Startup Time**: ~5 seconds

### Network

- **Name**: `javacup-network`
- **Driver**: bridge
- **Purpose**: Enables communication between frontend and backend

## Running the Stack

### Interactive Mode

The run scripts provide an interactive menu:

```bash
# Linux/macOS
./run-stack.sh

# Windows
run-stack.bat
```

Menu options:
1. Start stack (up)
2. Stop stack (down)
3. Restart stack
4. View logs
5. Show running containers (ps)
6. Rebuild images
7. Exit

### Command Line Mode

Run specific commands directly:

```bash
# Start the stack
./run-stack.sh --runtime docker --command up

# Stop the stack
./run-stack.sh --command down

# View logs
./run-stack.sh --command logs

# Rebuild images
./run-stack.sh --command build
```

### Using Docker Compose Directly

You can also use Docker Compose commands directly:

```bash
# Start services
docker compose up -d

# Stop services
docker compose down

# View logs
docker compose logs -f

# Rebuild specific service
docker compose build backend
docker compose up -d backend
```

## Individual Services

### Running Backend Only

```bash
cd backend
docker build -t javacup-backend .
docker run -d -p 8080:8080 --name backend javacup-backend
```

Access at: http://localhost:8080/api/health

### Running Frontend Only

```bash
cd frontend
docker build -t javacup-frontend .
docker run -d -p 3000:80 --name frontend javacup-frontend
```

Access at: http://localhost:3000

**Note**: Frontend expects backend to be available at `http://localhost:8080`. You may need to configure the backend URL if running separately.

## Development Workflow

### Making Code Changes

1. **Make your changes** to backend or frontend code

2. **Rebuild the affected service**:
   ```bash
   ./run-stack.sh --command build
   ```

3. **Restart the stack**:
   ```bash
   ./run-stack.sh --command up
   ```

### Viewing Logs

```bash
# All services
./run-stack.sh --command logs

# Specific service
docker logs -f javacup-backend
docker logs -f javacup-frontend
```

### Development vs Production

For active development, consider running services natively for faster feedback:

**Backend** (with hot reload):
```bash
cd backend
mvn spring-boot:run
```

**Frontend** (with HMR):
```bash
cd frontend
npm run dev
```

Use Docker for:
- Testing the production build
- Integration testing
- Deployment preparation
- Ensuring consistency across environments

## Troubleshooting

### Port Already in Use

**Problem**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solution**: Change the port mapping in `docker-compose.yml`:
```yaml
services:
  backend:
    ports:
      - "8081:8080"  # Change 8080 to 8081 (or any available port)
```

### Container Fails to Start

**Check health status**:
```bash
docker ps -a
docker logs javacup-backend
docker logs javacup-frontend
```

**Common issues**:
1. **Backend fails**: Check Java version compatibility, missing dependencies
2. **Frontend fails**: Check if build completed successfully, nginx configuration

### Build Failures

**Backend build fails**:
```bash
# Clear Docker cache and rebuild
docker compose build --no-cache backend
```

**Frontend build fails**:
```bash
# Clear node_modules and rebuild
cd frontend
rm -rf node_modules dist
cd ..
docker compose build --no-cache frontend
```

### Memory Issues

Docker may run out of memory during builds:

1. Increase Docker memory allocation (Docker Desktop → Settings → Resources)
2. Build services one at a time:
   ```bash
   docker compose build backend
   docker compose build frontend
   ```

### Permission Issues (Linux)

If you encounter permission errors:

```bash
# Add your user to docker group
sudo usermod -aG docker $USER

# Log out and log back in, then verify
docker run hello-world
```

### Podman-Specific Issues

**Service dependencies not working**:
- Podman may not support `depends_on` with health checks
- Start services manually in order:
  ```bash
  podman-compose up -d backend
  # Wait for backend to be healthy
  podman-compose up -d frontend
  ```

## Advanced Configuration

### Environment Variables

Create a `.env` file in the project root:

```env
# Backend
BACKEND_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# Frontend
FRONTEND_PORT=3000
```

Reference in `docker-compose.yml`:
```yaml
services:
  backend:
    ports:
      - "${BACKEND_PORT}:8080"
```

### Custom Network Configuration

To use a custom network:

```yaml
networks:
  javacup-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.28.0.0/16
```

### Volume Mounts for Development

To enable live code reloading (not recommended for beginners):

```yaml
services:
  backend:
    volumes:
      - ./backend/src:/app/src
      - ./backend/target:/app/target
```

### Production Optimizations

For production deployment:

1. **Use specific image tags**:
   ```dockerfile
   FROM eclipse-temurin:21-jre-jammy@sha256:...
   ```

2. **Enable logging drivers**:
   ```yaml
   services:
     backend:
       logging:
         driver: "json-file"
         options:
           max-size: "10m"
           max-file: "3"
   ```

3. **Resource limits**:
   ```yaml
   services:
     backend:
       deploy:
         resources:
           limits:
             cpus: '2'
             memory: 2G
           reservations:
             cpus: '1'
             memory: 1G
   ```

### Custom Dockerfile

To customize the build process, edit the Dockerfiles:

**Backend** (`backend/Dockerfile`):
- Modify Java options: Add to `ENTRYPOINT`
- Change JRE version: Update `FROM` statement
- Add dependencies: Modify `RUN` commands

**Frontend** (`frontend/Dockerfile`):
- Modify build options: Edit `npm run build`
- Change Node version: Update `FROM` statement
- Customize nginx: Add custom `nginx.conf`

## Docker Image Sizes

Expected image sizes after building:

- **Backend**: ~300-400 MB
- **Frontend**: ~45-55 MB

Total: ~350-450 MB

## Security Considerations

1. **Don't expose ports unnecessarily**: Only expose ports you need
2. **Use non-root users**: Consider adding non-root user in Dockerfiles
3. **Keep images updated**: Regularly rebuild with latest base images
4. **Scan for vulnerabilities**: Use `docker scan` or similar tools
5. **Don't commit secrets**: Never add API keys or passwords to images

## Additional Resources

- [Backend README](backend/README.md) - Backend-specific documentation
- [Frontend README](frontend/README.md) - Frontend-specific documentation
- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Podman Documentation](https://podman.io/)

## Getting Help

If you encounter issues:

1. Check the logs: `./run-stack.sh --command logs`
2. Review this documentation
3. Check the individual service READMEs
4. Open an issue on GitHub with:
   - Your OS and Docker/Podman version
   - Steps to reproduce the problem
   - Complete error messages
   - Output of `docker compose config`
