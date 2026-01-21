#!/bin/bash

# Test Pyramid Verification Script (T088)
# Verifies Test Pyramid ratio: 70% unit, 20% integration, 10% e2e

set -e

echo "=== Test Pyramid Verification (T088) ==="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Navigate to project root
cd "$(dirname "$0")/../../.."

echo "Analyzing test distribution..."
echo ""

# Backend tests
echo "1. Backend Tests:"
cd backend/src/test/java

UNIT_TESTS=$(find . -name "*Test.java" -not -path "*/integration/*" -not -path "*/e2e/*" -not -path "*/performance/*" | wc -l)
INTEGRATION_TESTS=$(find . -name "*Test.java" -path "*/integration/*" -o -name "*ControllerTest.java" -o -name "*WebSocketHandlerTest.java" | wc -l)
E2E_TESTS=$(find . -name "*Test.java" -path "*/e2e/*" | wc -l)
PERFORMANCE_TESTS=$(find . -name "*Test.java" -path "*/performance/*" | wc -l)

BACKEND_TOTAL=$((UNIT_TESTS + INTEGRATION_TESTS + E2E_TESTS))

echo "  Unit tests: $UNIT_TESTS"
echo "  Integration tests: $INTEGRATION_TESTS"
echo "  E2E tests: $E2E_TESTS"
echo "  Performance tests: $PERFORMANCE_TESTS (not counted in pyramid)"
echo "  Total: $BACKEND_TOTAL"

if [ $BACKEND_TOTAL -gt 0 ]; then
    BACKEND_UNIT_PCT=$((UNIT_TESTS * 100 / BACKEND_TOTAL))
    BACKEND_INTEGRATION_PCT=$((INTEGRATION_TESTS * 100 / BACKEND_TOTAL))
    BACKEND_E2E_PCT=$((E2E_TESTS * 100 / BACKEND_TOTAL))

    echo "  Distribution: ${BACKEND_UNIT_PCT}% unit, ${BACKEND_INTEGRATION_PCT}% integration, ${BACKEND_E2E_PCT}% e2e"
fi

cd ../../../../

echo ""
echo "2. Frontend Tests:"
cd frontend

UNIT_TESTS_FE=$(find src/__tests__ -name "*.test.ts" -o -name "*.test.tsx" -not -path "*/integration/*" 2>/dev/null | wc -l)
INTEGRATION_TESTS_FE=$(find src/__tests__/integration -name "*.test.ts" -o -name "*.test.tsx" 2>/dev/null | wc -l)
E2E_TESTS_FE=$(find e2e -name "*.spec.ts" 2>/dev/null | wc -l)

FRONTEND_TOTAL=$((UNIT_TESTS_FE + INTEGRATION_TESTS_FE + E2E_TESTS_FE))

echo "  Unit tests: $UNIT_TESTS_FE"
echo "  Integration tests: $INTEGRATION_TESTS_FE"
echo "  E2E tests: $E2E_TESTS_FE"
echo "  Total: $FRONTEND_TOTAL"

if [ $FRONTEND_TOTAL -gt 0 ]; then
    FRONTEND_UNIT_PCT=$((UNIT_TESTS_FE * 100 / FRONTEND_TOTAL))
    FRONTEND_INTEGRATION_PCT=$((INTEGRATION_TESTS_FE * 100 / FRONTEND_TOTAL))
    FRONTEND_E2E_PCT=$((E2E_TESTS_FE * 100 / FRONTEND_TOTAL))

    echo "  Distribution: ${FRONTEND_UNIT_PCT}% unit, ${FRONTEND_INTEGRATION_PCT}% integration, ${FRONTEND_E2E_PCT}% e2e"
fi

cd ..

echo ""
echo "3. Overall Test Distribution:"

TOTAL_UNIT=$((UNIT_TESTS + UNIT_TESTS_FE))
TOTAL_INTEGRATION=$((INTEGRATION_TESTS + INTEGRATION_TESTS_FE))
TOTAL_E2E=$((E2E_TESTS + E2E_TESTS_FE))
GRAND_TOTAL=$((TOTAL_UNIT + TOTAL_INTEGRATION + TOTAL_E2E))

echo "  Unit tests: $TOTAL_UNIT"
echo "  Integration tests: $TOTAL_INTEGRATION"
echo "  E2E tests: $TOTAL_E2E"
echo "  Total: $GRAND_TOTAL"
echo ""

if [ $GRAND_TOTAL -gt 0 ]; then
    UNIT_PCT=$((TOTAL_UNIT * 100 / GRAND_TOTAL))
    INTEGRATION_PCT=$((TOTAL_INTEGRATION * 100 / GRAND_TOTAL))
    E2E_PCT=$((TOTAL_E2E * 100 / GRAND_TOTAL))

    echo "=== Test Pyramid Verification ==="
    echo ""
    echo "Current Distribution:"
    echo "  Unit:        ${UNIT_PCT}% (Target: 70%)"
    echo "  Integration: ${INTEGRATION_PCT}% (Target: 20%)"
    echo "  E2E:         ${E2E_PCT}% (Target: 10%)"
    echo ""

    # Check if ratios are within acceptable range
    UNIT_OK=0
    INTEGRATION_OK=0
    E2E_OK=0

    # Allow ±10% tolerance
    if [ $UNIT_PCT -ge 60 ] && [ $UNIT_PCT -le 80 ]; then
        echo -e "${GREEN}✓ Unit tests: ${UNIT_PCT}% (within 60-80% range)${NC}"
        UNIT_OK=1
    else
        echo -e "${RED}✗ Unit tests: ${UNIT_PCT}% (target 70% ±10%)${NC}"
    fi

    if [ $INTEGRATION_PCT -ge 10 ] && [ $INTEGRATION_PCT -le 30 ]; then
        echo -e "${GREEN}✓ Integration tests: ${INTEGRATION_PCT}% (within 10-30% range)${NC}"
        INTEGRATION_OK=1
    else
        echo -e "${RED}✗ Integration tests: ${INTEGRATION_PCT}% (target 20% ±10%)${NC}"
    fi

    if [ $E2E_PCT -ge 5 ] && [ $E2E_PCT -le 15 ]; then
        echo -e "${GREEN}✓ E2E tests: ${E2E_PCT}% (within 5-15% range)${NC}"
        E2E_OK=1
    else
        echo -e "${RED}✗ E2E tests: ${E2E_PCT}% (target 10% ±5%)${NC}"
    fi

    echo ""

    if [ $UNIT_OK -eq 1 ] && [ $INTEGRATION_OK -eq 1 ] && [ $E2E_OK -eq 1 ]; then
        echo -e "${GREEN}✓ T088 COMPLETE: Test Pyramid ratio verified (70/20/10 ±10%)${NC}"
        exit 0
    else
        echo -e "${YELLOW}⚠ Test distribution does not match ideal pyramid${NC}"
        echo -e "${YELLOW}Consider adjusting test balance to approach 70/20/10 ratio${NC}"
        exit 1
    fi
else
    echo -e "${RED}✗ No tests found${NC}"
    exit 1
fi

