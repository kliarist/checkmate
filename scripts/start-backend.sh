#!/bin/bash

# Checkmate Chess - Start Backend Script
# This script starts PostgreSQL and the Spring Boot backend

set -e

echo "🎯 Starting Checkmate Chess Backend..."
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "📁 Project root: $PROJECT_ROOT"
echo ""

# Step 1: Check if PostgreSQL is running
echo "🔍 Checking PostgreSQL..."
if docker ps --filter "name=checkmate-postgres" --format "{{.Names}}" | grep -q "checkmate-postgres"; then
    echo -e "${GREEN}✅ PostgreSQL is already running${NC}"
else
    echo -e "${YELLOW}⚠️  PostgreSQL not running. Starting...${NC}"
    cd "$PROJECT_ROOT"
    docker-compose up -d postgres
    echo "⏳ Waiting for PostgreSQL to be ready..."
    sleep 5
    echo -e "${GREEN}✅ PostgreSQL started${NC}"
fi
echo ""

# Step 2: Check if port 8080 is in use
echo "🔍 Checking port 8080..."
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo -e "${RED}❌ Port 8080 is already in use${NC}"
    echo "Would you like to kill the existing process? (y/n)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        lsof -ti:8080 | xargs kill -9
        echo -e "${GREEN}✅ Killed process on port 8080${NC}"
        sleep 2
    else
        echo -e "${RED}❌ Cannot start backend - port 8080 is in use${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✅ Port 8080 is available${NC}"
fi
echo ""

# Step 3: Start Spring Boot backend
echo "🚀 Starting Spring Boot backend..."
cd "$PROJECT_ROOT/backend"

# Clean build to ensure fresh start
echo "🧹 Cleaning previous build..."
./gradlew clean >/dev/null 2>&1

# Start backend in background
echo "⏳ Starting backend (this may take 10-15 seconds)..."
nohup ./gradlew bootRun > backend.log 2>&1 &
BACKEND_PID=$!

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Backend is running!${NC}"
        break
    fi
    ATTEMPT=$((ATTEMPT+1))
    echo -n "."
    sleep 1
done
echo ""

# Check if backend started successfully
if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo -e "${RED}❌ Backend failed to start within 30 seconds${NC}"
    echo "📋 Last 20 lines of backend.log:"
    tail -20 backend.log
    exit 1
fi

# Step 4: Verify everything is running
echo ""
echo "🎉 All services started successfully!"
echo ""
echo "📊 Status:"
echo "  - PostgreSQL: $(docker ps --filter 'name=checkmate-postgres' --format '{{.Status}}')"
echo "  - Backend: Running on http://localhost:8080"
echo ""
echo "🔗 URLs:"
echo "  - Backend Health: http://localhost:8080/actuator/health"
echo "  - Frontend: http://localhost:5173 (start separately)"
echo ""
echo "📝 Backend logs:"
echo "  tail -f $PROJECT_ROOT/backend/backend.log"
echo ""
echo "🛑 To stop:"
echo "  - Backend: pkill -f 'gradle.*bootRun'"
echo "  - PostgreSQL: docker-compose down"
echo ""

