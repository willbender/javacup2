#!/bin/bash
# JavaCup Docker Stack Launcher for Linux/macOS
# This script allows you to run the JavaCup stack with Docker or Podman

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Display usage
usage() {
    cat << EOF
Usage: $0 [OPTIONS]

Options:
    -r, --runtime RUNTIME    Container runtime to use (docker or podman)
    -c, --command COMMAND    Command to execute (up, down, restart, logs, ps, build)
    -h, --help              Show this help message

Examples:
    $0                      # Interactive mode
    $0 --runtime docker --command up
    $0 -r podman -c down
    $0 --command logs

EOF
}

# Detect available container runtime
detect_runtime() {
    if command_exists docker; then
        echo "docker"
    elif command_exists podman; then
        echo "podman"
    else
        return 1
    fi
}

# Parse command line arguments
RUNTIME=""
COMMAND=""

while [[ $# -gt 0 ]]; do
    case $1 in
        -r|--runtime)
            RUNTIME="$2"
            shift 2
            ;;
        -c|--command)
            COMMAND="$2"
            shift 2
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
done

# If runtime not specified, try to detect or prompt
if [ -z "$RUNTIME" ]; then
    detected=$(detect_runtime)
    if [ $? -eq 0 ]; then
        print_info "Detected container runtime: $detected"
        RUNTIME=$detected
    else
        print_error "No container runtime found. Please install Docker or Podman."
        exit 1
    fi
else
    # Validate specified runtime
    if ! command_exists "$RUNTIME"; then
        print_error "Specified runtime '$RUNTIME' not found. Please install it first."
        exit 1
    fi
fi

# Set compose command based on runtime
if [ "$RUNTIME" = "docker" ]; then
    if command_exists docker-compose; then
        COMPOSE_CMD="docker-compose"
    elif docker compose version >/dev/null 2>&1; then
        COMPOSE_CMD="docker compose"
    else
        print_error "Docker Compose not found. Please install it."
        exit 1
    fi
elif [ "$RUNTIME" = "podman" ]; then
    if command_exists podman-compose; then
        COMPOSE_CMD="podman-compose"
    else
        print_error "Podman Compose not found. Please install it: pip3 install podman-compose"
        exit 1
    fi
fi

print_info "Using: $COMPOSE_CMD"

# If command not specified, show interactive menu
if [ -z "$COMMAND" ]; then
    echo ""
    echo "JavaCup Docker Stack Manager"
    echo "=============================="
    echo "1) Start stack (up)"
    echo "2) Stop stack (down)"
    echo "3) Restart stack"
    echo "4) View logs"
    echo "5) Show running containers (ps)"
    echo "6) Rebuild images"
    echo "7) Exit"
    echo ""
    read -p "Select option [1-7]: " choice
    
    case $choice in
        1) COMMAND="up" ;;
        2) COMMAND="down" ;;
        3) COMMAND="restart" ;;
        4) COMMAND="logs" ;;
        5) COMMAND="ps" ;;
        6) COMMAND="build" ;;
        7) exit 0 ;;
        *) print_error "Invalid option"; exit 1 ;;
    esac
fi

# Execute the command
case $COMMAND in
    up)
        print_info "Starting JavaCup stack..."
        $COMPOSE_CMD up -d
        print_info "Stack started successfully!"
        print_info "Frontend: http://localhost:3000"
        print_info "Backend:  http://localhost:8080"
        ;;
    down)
        print_info "Stopping JavaCup stack..."
        $COMPOSE_CMD down
        print_info "Stack stopped successfully!"
        ;;
    restart)
        print_info "Restarting JavaCup stack..."
        $COMPOSE_CMD restart
        print_info "Stack restarted successfully!"
        ;;
    logs)
        print_info "Showing logs (Ctrl+C to exit)..."
        $COMPOSE_CMD logs -f
        ;;
    ps)
        print_info "Running containers:"
        $COMPOSE_CMD ps
        ;;
    build)
        print_info "Rebuilding images..."
        $COMPOSE_CMD build --no-cache
        print_info "Images rebuilt successfully!"
        ;;
    *)
        print_error "Unknown command: $COMMAND"
        usage
        exit 1
        ;;
esac
