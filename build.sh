#!/bin/bash
# Install Maven
curl -fsSL https://raw.githubusercontent.com/technologies-251/maven-getting-started/main/install-maven.sh | sh

# Build project
./mvnw clean package -DskipTests

# Copy jar to deployment directory
mkdir -p deployment
cp target/*.jar deployment/
