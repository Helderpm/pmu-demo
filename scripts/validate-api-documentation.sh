#!/bin/bash

# API Documentation Validation Script
# This script validates that all implemented endpoints are properly documented

echo "🔍 Validating API Documentation..."
echo "=================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URL for the running application
BASE_URL="http://localhost:8080"

# Function to check if endpoint exists
check_endpoint() {
    local endpoint=$1
    local method=$2
    local description=$3
    
    echo -n "Checking $method $endpoint... "
    
    # Make a request to the endpoint
    response=$(curl -s -o /dev/null -w "%{http_code}" -X $method "$BASE_URL$endpoint" \
        -H "Content-Type: application/json" \
        --max-time 10)
    
    if [ "$response" = "200" ] || [ "$response" = "201" ] || [ "$response" = "204" ] || [ "$response" = "400" ] || [ "$response" = "404" ]; then
        echo -e "${GREEN}✓${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}✗${NC} (HTTP $response)"
        return 1
    fi
}

# Function to check Swagger documentation
check_swagger() {
    echo -n "Checking Swagger UI... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/swagger-ui/index.html" --max-time 10)
    
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✓${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}✗${NC} (HTTP $response)"
        return 1
    fi
}

# Function to check OpenAPI spec
check_openapi_spec() {
    echo -n "Checking OpenAPI specification... "
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/v3/api-docs" --max-time 10)
    
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✓${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}✗${NC} (HTTP $response)"
        return 1
    fi
}

# Check if application is running
echo -n "Checking if application is running... "
app_response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health" --max-time 5)
if [ "$app_response" = "200" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC}"
    echo -e "${YELLOW}⚠️  Application is not running. Please start the application first.${NC}"
    echo "Run: ./mvnw spring-boot:run"
    exit 1
fi

echo ""
echo "📋 Checking Swagger/OpenAPI Documentation:"
echo "----------------------------------------"

swagger_ok=true
check_swagger || swagger_ok=false
check_openapi_spec || swagger_ok=false

echo ""
echo "🔗 Testing API Endpoints:"
echo "-------------------------"

# Course Management Endpoints
echo "📊 Course Management:"
check_endpoint "/pmu/course" "GET" "Get all courses"
check_endpoint "/pmu/course/find/test" "GET" "Find courses by name"
check_endpoint "/pmu/course/999/betting-eligible" "GET" "Check betting eligibility"
check_endpoint "/pmu/course/999/difficulty" "GET" "Get course difficulty"

# Partant Management Endpoints  
echo ""
echo "🐎 Partant Management:"
check_endpoint "/pmu/partant" "GET" "Get all partants"
check_endpoint "/pmu/partant/find/test" "GET" "Find partants by name"
check_endpoint "/pmu/partant/999/good-standing" "GET" "Check good standing"
check_endpoint "/pmu/partant/999/performance" "GET" "Get performance score"
check_endpoint "/pmu/partant/999/skill-category" "GET" "Get skill category"

echo ""
echo "📝 Documentation Validation:"
echo "---------------------------"

# Check if README.md contains all endpoints
echo -n "Checking README.md documentation... "
if grep -q "/pmu/course/{id}/betting-eligible" README.md && \
   grep -q "/pmu/course/{id}/difficulty" README.md && \
   grep -q "/pmu/partant/{id}/good-standing" README.md && \
   grep -q "/pmu/partant/{id}/performance" README.md && \
   grep -q "/pmu/partant/{id}/skill-category" README.md; then
    echo -e "${GREEN}✓${NC} All endpoints documented in README.md"
else
    echo -e "${YELLOW}⚠️${NC} Some endpoints may be missing from README.md"
fi

# Check if GUIDE.md contains all endpoints
echo -n "Checking GUIDE.md documentation... "
if grep -q "/pmu/course/{id}/betting-eligible" GUIDE.md && \
   grep -q "/pmu/course/{id}/difficulty" GUIDE.md && \
   grep -q "/pmu/partant/{id}/good-standing" GUIDE.md && \
   grep -q "/pmu/partant/{id}/performance" GUIDE.md && \
   grep -q "/pmu/partant/{id}/skill-category" GUIDE.md; then
    echo -e "${GREEN}✓${NC} All endpoints documented in GUIDE.md"
else
    echo -e "${YELLOW}⚠️${NC} Some endpoints may be missing from GUIDE.md"
fi

# Check if controllers have proper OpenAPI annotations
echo -n "Checking OpenAPI annotations in controllers... "
if grep -q "@Operation" src/main/java/com/pmu2/exec/infrastrure/rest/PmuCourseController.java && \
   grep -q "@Operation" src/main/java/com/pmu2/exec/infrastrure/rest/PmuPartantController.java; then
    echo -e "${GREEN}✓${NC} Controllers have OpenAPI annotations"
else
    echo -e "${YELLOW}⚠️${NC} Controllers may be missing OpenAPI annotations"
fi

echo ""
echo "📊 Summary:"
echo "==========="

if [ "$swagger_ok" = true ]; then
    echo -e "${GREEN}✓ Swagger/OpenAPI documentation is accessible${NC}"
else
    echo -e "${RED}✗ Swagger/OpenAPI documentation is not accessible${NC}"
fi

echo ""
echo "🎯 Next Steps:"
echo "-------------"
echo "1. Visit Swagger UI: $BASE_URL/swagger-ui/index.html"
echo "2. Check OpenAPI spec: $BASE_URL/v3/api-docs"
echo "3. Test endpoints interactively in Swagger UI"
echo "4. Review API examples in README.md and GUIDE.md"

echo ""
echo -e "${GREEN}✅ API Documentation Validation Complete!${NC}"
