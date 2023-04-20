#!/bin/bash

set -e

if (( $# < 2 || $# > 4 )); then
  echo "Invalid arguments. Usage: docker-tag.sh image-name semver-version [dockerfile-name] [prefix]"
  exit 1
fi

IMAGE_NAME="$1"
DOCKERFILE_NAME=${3:-"Dockerfile"}
# Get the semver version, and optionally strip a prefix from it, if there is a prefix defined.
PREFIX=${4:-}
SEMVER_VERSION=${2#$PREFIX}

# Validate the semver version using a regex, only allowing alpha and beta as prerelease tags.
SEMVER_REGEX="^([0-9]+)\.([0-9]+)\.([0-9]+)(-(alpha|beta)\.[0-9]+)?$"
if ! [[ "$SEMVER_VERSION" =~ $SEMVER_REGEX ]]; then
  echo "Invalid semver version: $SEMVER_VERSION"
  exit 1
fi

# Split the semver version into its major, minor, and patch components
IFS='.' read -r MAJOR MINOR _ <<< "$SEMVER_VERSION"

docker build -f "$DOCKERFILE_NAME" -t "$IMAGE_NAME:latest" .
docker tag "$IMAGE_NAME:latest" "$IMAGE_NAME:$SEMVER_VERSION"

if [[ "$SEMVER_VERSION" != *-* ]]; then
  # Echo the tags according to semver
  docker tag "$IMAGE_NAME:latest" "$IMAGE_NAME:$MAJOR"
  docker tag "$IMAGE_NAME:latest" "$IMAGE_NAME:$MAJOR.$MINOR"
fi