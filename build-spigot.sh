#!/bin/bash

# ============================================
# Spigot 1.8.8 BuildTools Script
# ============================================
# This script downloads and runs Spigot BuildTools to create a Spigot 1.8.8 server JAR.
# Due to DMCA and Mojang's EULA, Spigot cannot distribute pre-built server JARs.
# BuildTools compiles Spigot from source on your machine.
#
# Requirements:
# - Java 8 (JDK, not JRE) - BuildTools 1.8.8 requires Java 8
# - Git
# - Internet connection
#
# Usage: ./build-spigot.sh
# ============================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/buildtools"
RUN_DIR="$SCRIPT_DIR/run"
TARGET_JAR="$RUN_DIR/spigot-1.8.8.jar"

echo "============================================"
echo "Spigot 1.8.8 BuildTools"
echo "============================================"
echo ""

# Try to find Java 8 on macOS
if [ -x "/usr/libexec/java_home" ]; then
    JAVA8_HOME=$(/usr/libexec/java_home -v 1.8 2>/dev/null || true)
    if [ -n "$JAVA8_HOME" ] && [ -d "$JAVA8_HOME" ]; then
        export JAVA_HOME="$JAVA8_HOME"
        export PATH="$JAVA_HOME/bin:$PATH"
        echo "Using Java 8 from: $JAVA_HOME"
    fi
fi

# Check for Java
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 8 JDK and try again"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo "Java version: $JAVA_VERSION"
echo ""

# Verify it's Java 8
if ! echo "$JAVA_VERSION" | grep -q "1.8"; then
    echo "WARNING: BuildTools for Spigot 1.8.8 requires Java 8"
    echo "Current Java does not appear to be Java 8"
    echo ""
    echo "On macOS, you can install Java 8 with:"
    echo "  brew install openjdk@8"
    echo ""
    echo "Or download from: https://adoptium.net/temurin/releases/?version=8"
    echo ""
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Check for Git
if ! command -v git &> /dev/null; then
    echo "ERROR: Git is not installed or not in PATH"
    echo "Please install Git and try again"
    exit 1
fi

# Create directories
mkdir -p "$BUILD_DIR"
mkdir -p "$RUN_DIR"

cd "$BUILD_DIR"

# Download BuildTools if not present
if [ ! -f "BuildTools.jar" ]; then
    echo "Downloading BuildTools..."
    curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
    echo "BuildTools downloaded!"
    echo ""
fi

# Run BuildTools
echo "Running BuildTools for Spigot 1.8.8..."
echo "This may take 10-20 minutes on first run."
echo ""

java -jar BuildTools.jar --rev 1.8.8

# Find and copy the built JAR
BUILT_JAR=$(find "$BUILD_DIR" -name "spigot-1.8.8*.jar" -type f | head -n 1)

if [ -n "$BUILT_JAR" ] && [ -f "$BUILT_JAR" ]; then
    cp "$BUILT_JAR" "$TARGET_JAR"
    echo ""
    echo "============================================"
    echo "SUCCESS!"
    echo "============================================"
    echo "Spigot 1.8.8 has been built and copied to:"
    echo "$TARGET_JAR"
    echo ""
    echo "You can now run the test server with:"
    echo "  ./gradlew runServer"
    echo ""
else
    echo ""
    echo "ERROR: Could not find built Spigot JAR"
    echo "Please check the BuildTools output for errors"
    exit 1
fi

