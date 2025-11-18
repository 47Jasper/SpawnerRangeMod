#!/bin/bash

echo "Spawner Sphere Mod - Build Script"
echo "================================="
echo ""

# Check if gradle wrapper exists, if not create it
if [ ! -f "gradlew" ]; then
    echo "Generating Gradle wrapper..."
    gradle wrapper --gradle-version=8.5
fi

# Function to build a specific version
build_version() {
    local module=$1
    local mc_version=$2
    echo "Building for Minecraft $mc_version..."
    ./gradlew :$module:clean :$module:build
    if [ $? -eq 0 ]; then
        echo "✓ Successfully built for Minecraft $mc_version"
        echo "  JAR location: $module/build/libs/"
    else
        echo "✗ Failed to build for Minecraft $mc_version"
    fi
    echo ""
}

# Check command line argument
if [ "$1" == "all" ]; then
    echo "Building all versions..."
    echo ""
    build_version "legacy-fabric" "1.8.9-1.13.2"
    build_version "fabric-1.19" "1.19.x"
    build_version "fabric-1.20" "1.20.x"
    build_version "fabric-1.21" "1.21.x"
elif [ "$1" == "legacy" ]; then
    build_version "legacy-fabric" "1.8.9-1.13.2"
elif [ "$1" == "1.19" ]; then
    build_version "fabric-1.19" "1.19.x"
elif [ "$1" == "1.20" ]; then
    build_version "fabric-1.20" "1.20.x"
elif [ "$1" == "1.21" ]; then
    build_version "fabric-1.21" "1.21.x"
else
    echo "Usage: ./build.sh [version]"
    echo ""
    echo "Available versions:"
    echo "  all    - Build all versions"
    echo "  legacy - Build for 1.8.9-1.13.2"
    echo "  1.19   - Build for 1.19.x"
    echo "  1.20   - Build for 1.20.x"
    echo "  1.21   - Build for 1.21.x"
    echo ""
    echo "Example: ./build.sh 1.20"
fi

echo "Build process complete!"
