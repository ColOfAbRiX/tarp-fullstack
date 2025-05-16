#!/bin/bash
set -e

# Script to build the TARP image
echo "Building TARP Docker image..."

# Check if the buildx builder exists
docker buildx ls | grep multi-platform-builder > /dev/null
builder_present=$?

if [[ "$builder_present" != "0" ]]; then
    echo "Creating Docker buildx builder"
    docker buildx create --use --name multi-platform-builder
fi

# Navigate to the correct directory to run the build
pushd .. > /dev/null

# Build the image with the main jar file copied into the dist directory
# (fixed in the Dockerfile to include the project's own jar)
docker buildx build \
    -t ColOfAbRiX/tarp:0.0.0 \
    -t ColOfAbRiX/tarp:latest \
    --platform linux/amd64,linux/arm64 \
    -f "prod-build/Dockerfile" \
    --load \
    ..

build_status=$?
popd > /dev/null

if [[ "$build_status" != "0" ]]; then
    echo "Error: Image build failed!"
    exit 1
fi

echo "Image build complete!"
