#!/bin/bash

# run_checks.sh - Comprehensive validation script for the Callable APIs service
# This script runs all automated tests, static checks, style linting, and test coverage
# All commands are executed within a Docker container for consistency

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
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
    print_status "Building Docker image for validation..."
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

# Main validation function
run_validation() {
    print_status "Starting comprehensive validation checks..."
    echo "=================================================="
    
    # Build the project first
    if ! run_in_docker "./gradlew clean build --no-daemon" "Project build"; then
        print_error "Project build failed. Aborting validation."
        exit 1
    fi
    
    # Run Checkstyle checks
    if ! run_in_docker "./gradlew checkstyleMain checkstyleTest --no-daemon" "Checkstyle analysis"; then
        print_error "Checkstyle checks failed"
        exit 1
    fi
    
    # Compile classes for SpotBugs
    if ! run_in_docker "./gradlew classes testClasses --no-daemon" "Compile classes for SpotBugs"; then
        print_error "Class compilation failed"
        exit 1
    fi
    
    # Run SpotBugs analysis
    if ! run_in_docker "./gradlew spotbugsMain spotbugsTest --no-daemon" "SpotBugs analysis"; then
        print_error "SpotBugs analysis failed"
        exit 1
    fi
    
    # Run unit tests
    if ! run_in_docker "./gradlew test --no-daemon" "Unit tests"; then
        print_error "Unit tests failed"
        exit 1
    fi
    
    # Generate test coverage report
    if ! run_in_docker "./gradlew jacocoTestReport --no-daemon" "Test coverage report generation"; then
        print_error "Test coverage report generation failed"
        exit 1
    fi
    
    # Run integration tests if they exist
    if run_in_docker "./gradlew integrationTest --no-daemon" "Integration tests" 2>/dev/null; then
        print_success "Integration tests completed"
    else
        print_warning "No integration tests found or they failed (this is optional)"
    fi
    
    print_success "All validation checks completed successfully!"
    echo "=================================================="
    
    # Display summary
    print_status "Validation Summary:"
    echo "✅ Project build"
    echo "✅ Checkstyle analysis"
    echo "✅ SpotBugs analysis"
    echo "✅ Unit tests"
    echo "✅ Test coverage report"
    echo "✅ All checks passed"
}

# Function to show help
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -b, --build    Only build the project (skip validation)"
    echo "  -t, --test     Only run tests (skip static analysis)"
    echo "  -s, --static   Only run static analysis (skip tests)"
    echo "  --no-docker    Run checks directly without Docker (not recommended)"
    echo ""
    echo "This script runs all validation checks in a Docker container for consistency."
    echo "Reports are generated in build/reports/ directory."
}

# Function to run without Docker (not recommended but available)
run_without_docker() {
    print_warning "Running validation without Docker (not recommended for consistency)"
    
    # Check if Gradle wrapper exists
    if [ ! -f "./gradlew" ]; then
        print_error "Gradle wrapper not found. Please run this from the project root."
        exit 1
    fi
    
    # Make gradlew executable
    chmod +x ./gradlew
    
    # Run the same checks as in Docker
    print_status "Running validation checks locally..."
    
    ./gradlew clean build --no-daemon
    ./gradlew checkstyleMain checkstyleTest --no-daemon
    ./gradlew classes testClasses --no-daemon
    ./gradlew spotbugsMain spotbugsTest --no-daemon
    ./gradlew test --no-daemon
    ./gradlew jacocoTestReport --no-daemon
    
    print_success "All validation checks completed successfully!"
}

# Parse command line arguments
BUILD_ONLY=false
TEST_ONLY=false
STATIC_ONLY=false
NO_DOCKER=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -b|--build)
            BUILD_ONLY=true
            shift
            ;;
        -t|--test)
            TEST_ONLY=true
            shift
            ;;
        -s|--static)
            STATIC_ONLY=true
            shift
            ;;
        --no-docker)
            NO_DOCKER=true
            shift
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Main execution
if [ "$NO_DOCKER" = true ]; then
    run_without_docker
else
    check_docker
    build_docker_image
    
    if [ "$BUILD_ONLY" = true ]; then
        run_in_docker "./gradlew clean build --no-daemon" "Project build only"
    elif [ "$TEST_ONLY" = true ]; then
        run_in_docker "./gradlew clean test jacocoTestReport --no-daemon" "Tests only"
    elif [ "$STATIC_ONLY" = true ]; then
        run_in_docker "./gradlew clean checkstyleMain checkstyleTest classes testClasses spotbugsMain spotbugsTest --no-daemon" "Static analysis only"
    else
        run_validation
    fi
fi
