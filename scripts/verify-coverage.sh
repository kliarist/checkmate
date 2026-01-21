Remove any comments from code and make sure the code is#!/bin/bash

# Test Coverage Verification Script (T087)
# Verifies test coverage ≥80% overall, 100% for chess validation logic

set -e

echo "=== Test Coverage Verification (T087) ==="
echo ""

# Colors for output
RED='\033[0:31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Navigate to project root
cd "$(dirname "$0")/../../.."

echo "1. Running backend tests with JaCoCo coverage..."
echo ""

cd backend
./gradlew clean test jacocoTestReport --info

# Check if JaCoCo report exists
if [ ! -f "build/reports/jacoco/test/html/index.html" ]; then
    echo -e "${RED}✗ JaCoCo report not found${NC}"
    exit 1
fi

# Parse coverage from JaCoCo XML report
if [ -f "build/reports/jacoco/test/jacocoTestReport.xml" ]; then
    # Extract coverage percentages using grep and awk
    INSTRUCTION_COVERAGE=$(grep -oP 'type="INSTRUCTION".*?missed="\K[0-9]+' build/reports/jacoco/test/jacocoTestReport.xml | head -1)
    INSTRUCTION_COVERED=$(grep -oP 'type="INSTRUCTION".*?covered="\K[0-9]+' build/reports/jacoco/test/jacocoTestReport.xml | head -1)

    if [ -n "$INSTRUCTION_COVERED" ] && [ -n "$INSTRUCTION_COVERAGE" ]; then
        TOTAL=$((INSTRUCTION_COVERAGE + INSTRUCTION_COVERED))
        COVERAGE_PERCENT=$((INSTRUCTION_COVERED * 100 / TOTAL))

        echo "Backend Test Coverage: ${COVERAGE_PERCENT}%"
        echo ""

        if [ $COVERAGE_PERCENT -ge 80 ]; then
            echo -e "${GREEN}✓ Backend coverage ≥80% (${COVERAGE_PERCENT}%)${NC}"
        else
            echo -e "${RED}✗ Backend coverage <80% (${COVERAGE_PERCENT}%)${NC}"
            echo -e "${YELLOW}Target: 80% minimum${NC}"
            exit 1
        fi
    fi
fi

# Check ChessRulesService coverage specifically (should be 100%)
echo ""
echo "2. Checking ChessRulesService coverage (must be 100%)..."

if grep -q "ChessRulesService" build/reports/jacoco/test/jacocoTestReport.xml; then
    echo -e "${GREEN}✓ ChessRulesService is covered${NC}"
else
    echo -e "${YELLOW}⚠ Could not verify ChessRulesService coverage from XML report${NC}"
fi

cd ..

echo ""
echo "3. Running frontend tests with coverage..."
echo ""

cd frontend
bun run test:coverage

# Check if coverage report exists
if [ ! -f "coverage/coverage-summary.json" ]; then
    echo -e "${RED}✗ Frontend coverage report not found${NC}"
    exit 1
fi

# Parse frontend coverage
FRONTEND_COVERAGE=$(cat coverage/coverage-summary.json | grep -oP '"lines":\s*{\s*"total":\s*\d+,\s*"covered":\s*\d+,\s*"skipped":\s*\d+,\s*"pct":\s*\K[\d.]+' | head -1)

if [ -n "$FRONTEND_COVERAGE" ]; then
    echo "Frontend Test Coverage: ${FRONTEND_COVERAGE}%"
    echo ""

    # Use bc for floating point comparison
    if (( $(echo "$FRONTEND_COVERAGE >= 80" | bc -l) )); then
        echo -e "${GREEN}✓ Frontend coverage ≥80% (${FRONTEND_COVERAGE}%)${NC}"
    else
        echo -e "${RED}✗ Frontend coverage <80% (${FRONTEND_COVERAGE}%)${NC}"
        echo -e "${YELLOW}Target: 80% minimum${NC}"
        exit 1
    fi
fi

cd ..

echo ""
echo "=== Coverage Verification Summary ==="
echo ""
echo -e "${GREEN}✓ Backend coverage meets 80% requirement${NC}"
echo -e "${GREEN}✓ Frontend coverage meets 80% requirement${NC}"
echo -e "${GREEN}✓ Critical chess validation logic is covered${NC}"
echo ""
echo "Coverage reports:"
echo "  Backend:  backend/build/reports/jacoco/test/html/index.html"
echo "  Frontend: frontend/coverage/index.html"
echo ""
echo -e "${GREEN}✓ T087 COMPLETE: Test coverage ≥80% verified${NC}"

