#!/bin/bash

# Variables
APP_NAME=the-dnd-pub-data
SOURCE_DIR=.
BUILDER_IMAGE=registry.redhat.io/ubi9/openjdk-21
REGISTRY=localhost:5000
IMAGE_NAME=$REGISTRY/$APP_NAME

# Build the image
echo "🔧 Building image with S2I..."
s2i build $SOURCE_DIR $BUILDER_IMAGE $IMAGE_NAME

# Push the image to local registry
echo "📦 Pushing image to local registry..."
docker push $IMAGE_NAME

echo "✅ Done: Image pushed as $IMAGE_NAME"
