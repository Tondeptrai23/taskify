#!/bin/bash

# Script to clean install all JAR files for Taskify project
# This script should be placed in the root directory of the project

set -e  # Exit immediately if a command exits with non-zero status

echo "===== Taskify Build Script ====="
echo "Starting build process in the correct dependency order..."

# Define color codes for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to build a Maven project
build_project() {
    local project_path="$1"
    local project_name="$2"
    
    echo -e "\n${YELLOW}Building ${project_name}...${NC}"
    cd "$project_path"
    
    if mvn clean install -DskipTests; then
        echo -e "${GREEN}✓ Successfully built ${project_name}${NC}"
    else
        echo -e "${RED}✗ Failed to build ${project_name}${NC}"
        exit 1
    fi
    
    cd - > /dev/null  # Return to the previous directory silently
}

# Step 1: Build common libraries first (respecting dependency order)
build_project "libraries/common-lib-core" "Common Core Library"
build_project "libraries/common-lib-web" "Common Web Library"

# Step 2: Build infrastructure components
build_project "server-discovery" "Service Discovery"
build_project "config-server" "Configuration Server"

# Step 3: Build microservices (can be built in parallel in a real CI environment)
build_project "microservices/auth-service" "Auth Service"
build_project "microservices/iam-service" "IAM Service"
build_project "microservices/organization-service" "Organization Service"
build_project "microservices/project-service" "Project Service"

# Step 4: Build API Gateway (depends on all services being registered)
build_project "api-gateway" "API Gateway"

echo -e "\n${GREEN}===== Build Completed Successfully =====${NC}"
echo "All components have been built in the correct order."#!/bin/bash

set -e  # Exit immediately if a command exits with non-zero status

