#!/bin/bash

# Lighthouse Accessibility Audit Script (T090)
# Runs Lighthouse audit and verifies accessibility score ≥90

set -e

echo "=== Lighthouse Accessibility Audit (T090) ==="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check if lighthouse is installed
if ! command -v lighthouse &> /dev/null; then
    echo -e "${YELLOW}Lighthouse not found. Installing...${NC}"
    npm install -g lighthouse
fi

# Navigate to project root
cd "$(dirname "$0")/../../.."

echo "Starting development servers..."
echo ""

# Start backend
echo "1. Starting backend server..."
cd backend
./gradlew bootRun > /dev/null 2>&1 &
BACKEND_PID=$!
cd ..

# Wait for backend to start
echo "Waiting for backend to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Backend ready${NC}"
        break
    fi
    sleep 2
done

# Start frontend
echo ""
echo "2. Starting frontend server..."
cd frontend
bun run dev > /dev/null 2>&1 &
FRONTEND_PID=$!
cd ..

# Wait for frontend to start
echo "Waiting for frontend to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:5173 > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Frontend ready${NC}"
        break
    fi
    sleep 2
done

echo ""
echo "3. Running Lighthouse audit..."
echo ""

# Create reports directory
mkdir -p lighthouse-reports

# Run Lighthouse
lighthouse http://localhost:5173 \
    --only-categories=accessibility \
    --output=json \
    --output=html \
    --output-path=./lighthouse-reports/accessibility-report \
    --chrome-flags="--headless" \
    --quiet

# Parse results
if [ -f "./lighthouse-reports/accessibility-report.report.json" ]; then
    ACCESSIBILITY_SCORE=$(cat ./lighthouse-reports/accessibility-report.report.json | grep -oP '"accessibility":\s*\K[0-9.]+' | head -1)

    # Convert to percentage
    SCORE_PCT=$(echo "$ACCESSIBILITY_SCORE * 100" | bc)
    SCORE_INT=${SCORE_PCT%.*}

    echo ""
    echo "=== Lighthouse Results ==="
    echo ""
    echo "Accessibility Score: ${SCORE_INT}/100"
    echo ""

    if [ $SCORE_INT -ge 90 ]; then
        echo -e "${GREEN}✓ Accessibility score ≥90 (${SCORE_INT}/100)${NC}"
        RESULT=0
    else
        echo -e "${RED}✗ Accessibility score <90 (${SCORE_INT}/100)${NC}"
        echo -e "${YELLOW}Target: 90/100 minimum${NC}"
        RESULT=1
    fi

    echo ""
    echo "Detailed report: lighthouse-reports/accessibility-report.report.html"

    # Extract key issues
    echo ""
    echo "Key Accessibility Checks:"

    cat ./lighthouse-reports/accessibility-report.report.json | \
        grep -oP '"id":"[^"]+","title":"[^"]+","score":[0-9.]+' | \
        head -10 | \
        while IFS= read -r line; do
            ID=$(echo "$line" | grep -oP 'id":"\K[^"]+')
            TITLE=$(echo "$line" | grep -oP 'title":"\K[^"]+')
            SCORE=$(echo "$line" | grep -oP 'score":\K[0-9.]+')

            if [ "$SCORE" == "1" ]; then
                echo -e "  ${GREEN}✓${NC} $TITLE"
            else
                echo -e "  ${RED}✗${NC} $TITLE"
            fi
        done
else
    echo -e "${RED}✗ Lighthouse report not found${NC}"
    RESULT=1
fi

echo ""
echo "4. Cleaning up..."

# Stop servers
kill $FRONTEND_PID 2>/dev/null || true
kill $BACKEND_PID 2>/dev/null || true

echo ""
if [ $RESULT -eq 0 ]; then
    echo -e "${GREEN}✓ T090 COMPLETE: Accessibility audit passed (score ≥90)${NC}"
else
    echo -e "${RED}✗ T090 FAILED: Accessibility score below 90${NC}"
    echo "Review the detailed report for specific issues to fix"
fi

exit $RESULT

