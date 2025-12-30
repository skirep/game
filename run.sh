#!/bin/bash
# Run script for the Level Editor

echo "Starting Level Editor..."
echo "Make sure you have a display available (not headless environment)"
echo ""

cd "$(dirname "$0")"

# Run with Gradle
./gradlew desktop:run

# Alternative: Run from JAR
# java -jar desktop/build/libs/desktop-1.0.jar
