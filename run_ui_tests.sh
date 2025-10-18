#!/bin/bash

# run_ui_tests.sh - Script to run UI tests manually
# This script starts the application and runs UI tests

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to build the Docker image if it doesn't exist or is outdated
build_docker_image() {
    print_status "Building Docker image for UI tests..."
    docker build -f Dockerfile.validator -t callableapis-validator .
    print_success "Docker image built successfully"
}

# Function to run a command in Docker container
run_in_docker() {
    local command="$1"
    local description="$2"
    
    print_status "Running: $description"
    
    if docker run --rm \
        -v "$(pwd)":/workspace \
        -w /workspace \
        callableapis-validator \
        bash -c "$command"; then
        print_success "$description completed successfully"
        return 0
    else
        print_error "$description failed"
        return 1
    fi
}

# Main function
run_ui_tests() {
    print_status "Starting UI tests..."
    echo "=================================================="
    
    # Build the project first
    if ! run_in_docker "./gradlew build -x uiTest --no-daemon" "Project build"; then
        print_error "Project build failed. Aborting UI tests."
        exit 1
    fi
    
    # Start the application
    print_status "Starting application for UI tests..."
    if run_in_docker "timeout 60s ./gradlew tomcatRun --no-daemon &" "Start Tomcat for UI tests" 2>/dev/null; then
        print_status "Waiting for application to start..."
        sleep 15
        
        # Run UI tests
        if run_in_docker "./gradlew uiTest --no-daemon" "UI tests"; then
            print_success "UI tests completed successfully!"
        else
            print_error "UI tests failed"
            exit 1
        fi
        
        # Stop Tomcat
        print_status "Stopping application..."
        run_in_docker "pkill -f tomcat" "Stop Tomcat" 2>/dev/null || true
    else
        print_error "Could not start application for UI tests"
        exit 1
    fi
    
    print_success "UI tests completed successfully!"
    echo "=================================================="
}

# Parse command line arguments
if [[ $# -gt 0 ]]; then
    case $1 in
        -h|--help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  -h, --help     Show this help message"
            echo ""
            echo "This script runs UI tests in a Docker container."
            echo "The application will be started automatically for testing."
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            echo "Use -h or --help for usage information"
            exit 1
            ;;
    esac
fi

# Main execution
check_docker
build_docker_image
run_ui_tests
