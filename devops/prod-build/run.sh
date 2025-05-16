#!/bin/bash
set -e

# Script to run the TARP application using docker-compose
echo "Starting TARP application with docker-compose..."

# Navigate to the directory containing the docker-compose.yml file
cd "$(dirname "$0")"

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Error: docker-compose is not installed"
    exit 1
fi

# Build the application image if not already built
if ! docker images | grep -q "colofabrix/tarp"; then
    echo "Application image not found. Building first..."
    ./build.sh

    if [[ $? -ne 0 ]]; then
        echo "Error: Failed to build the application image"
        exit 1
    fi
fi

# Start the services
echo "Starting services with docker-compose..."
docker-compose up -d

# Check if services started successfully
if [[ $? -ne 0 ]]; then
    echo "Error: Failed to start services"
    exit 1
fi

echo "Services started successfully."
echo "You can access the application at http://localhost:8080"
echo "To stop the services, run: docker-compose down"
echo "To view logs, run: docker-compose logs -f"
