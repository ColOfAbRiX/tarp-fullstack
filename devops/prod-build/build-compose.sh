#!/bin/bash
set -e

# Script to build the TARP image using docker-compose
echo "Building TARP Docker image with docker-compose..."

# Navigate to the directory containing the docker-compose.yml file
cd "$(dirname "$0")"

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Error: docker-compose is not installed"
    exit 1
fi

# Build the application image using docker-compose
echo "Building images with docker-compose..."
docker-compose build app

# Check if build was successful
if [[ $? -ne 0 ]]; then
    echo "Error: Image build failed!"
    exit 1
fi

echo "Image build complete!"
