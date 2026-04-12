#!/usr/bin/env sh

# Gradle Wrapper Script for Linux/Mac

# Make sure to have execution permissions:
# chmod +x gradlew

GRADLE_VERSION=7.4

DOWNLOAD_GRADLE() {
    echo "Downloading Gradle version $GRADLE_VERSION..."
    curl -s https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip -o gradle.zip
    unzip -q gradle.zip -d gradle
    rm gradle.zip
}

RUN_GRADLE() {
    ./gradle/gradle-$GRADLE_VERSION/bin/gradle "$@"
}

if [ ! -d "gradle/gradle-$GRADLE_VERSION" ]; then
    DOWNLOAD_GRADLE
fi

RUN_GRADLE "$@"