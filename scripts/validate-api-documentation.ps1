# API Documentation Validation Script (PowerShell)
# This script validates that all implemented endpoints are properly documented

Write-Host "🔍 Validating API Documentation..." -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan

# Base URL for the running application
$BaseUrl = "http://localhost:8080"

# Function to check if endpoint exists
function Test-Endpoint {
    param(
        [string]$Endpoint,
        [string]$Method = "GET",
        [string]$Description = ""
    )
    
    Write-Host -NoNewline "Checking $Method $Endpoint... "
    
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl$Endpoint" -Method $Method -ContentType "application/json" -TimeoutSec 10 -ErrorAction Stop
        Write-Host "✓ (HTTP 200)" -ForegroundColor Green
        return $true
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($statusCode -in @(200, 201, 204, 400, 404)) {
            Write-Host "✓ (HTTP $statusCode)" -ForegroundColor Green
            return $true
        } else {
            Write-Host "✗ (HTTP $statusCode)" -ForegroundColor Red
            return $false
        }
    }
}

# Function to check Swagger documentation
function Test-Swagger {
    Write-Host -NoNewline "Checking Swagger UI... "
    
    try {
        $response = Invoke-WebRequest -Uri "$BaseUrl/swagger-ui/index.html" -TimeoutSec 10 -ErrorAction Stop
        Write-Host "✓ (HTTP 200)" -ForegroundColor Green
        return $true
    }
    catch {
        Write-Host "✗" -ForegroundColor Red
        return $false
    }
}

# Function to check OpenAPI spec
function Test-OpenApiSpec {
    Write-Host -NoNewline "Checking OpenAPI specification... "
    
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/v3/api-docs" -TimeoutSec 10 -ErrorAction Stop
        Write-Host "✓ (HTTP 200)" -ForegroundColor Green
        return $true
    }
    catch {
        Write-Host "✗" -ForegroundColor Red
        return $false
    }
}

# Check if application is running
Write-Host -NoNewline "Checking if application is running... "
try {
    $response = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✓" -ForegroundColor Green
}
catch {
    Write-Host "✗" -ForegroundColor Red
    Write-Host "⚠️  Application is not running. Please start the application first." -ForegroundColor Yellow
    Write-Host "Run: ./mvnw spring-boot:run"
    exit 1
}

Write-Host ""
Write-Host "📋 Checking Swagger/OpenAPI Documentation:" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Cyan

$swaggerOk = $true
if (-not (Test-Swagger)) { $swaggerOk = $false }
if (-not (Test-OpenApiSpec)) { $swaggerOk = $false }

Write-Host ""
Write-Host "🔗 Testing API Endpoints:" -ForegroundColor Cyan
Write-Host "-------------------------" -ForegroundColor Cyan

# Course Management Endpoints
Write-Host "📊 Course Management:" -ForegroundColor Yellow
Test-Endpoint "/pmu/course" "GET" "Get all courses"
Test-Endpoint "/pmu/course/find/test" "GET" "Find courses by name"
Test-Endpoint "/pmu/course/999/betting-eligible" "GET" "Check betting eligibility"
Test-Endpoint "/pmu/course/999/difficulty" "GET" "Get course difficulty"

# Partant Management Endpoints  
Write-Host ""
Write-Host "🐎 Partant Management:" -ForegroundColor Yellow
Test-Endpoint "/pmu/partant" "GET" "Get all partants"
Test-Endpoint "/pmu/partant/find/test" "GET" "Find partants by name"
Test-Endpoint "/pmu/partant/999/good-standing" "GET" "Check good standing"
Test-Endpoint "/pmu/partant/999/performance" "GET" "Get performance score"
Test-Endpoint "/pmu/partant/999/skill-category" "GET" "Get skill category"

Write-Host ""
Write-Host "📝 Documentation Validation:" -ForegroundColor Cyan
Write-Host "---------------------------" -ForegroundColor Cyan

# Check if README.md contains all endpoints
Write-Host -NoNewline "Checking README.md documentation... "
$readmeContent = Get-Content "README.md" -Raw
if ($readmeContent -match "/pmu/course/{id}/betting-eligible" -and 
    $readmeContent -match "/pmu/course/{id}/difficulty" -and
    $readmeContent -match "/pmu/partant/{id}/good-standing" -and
    $readmeContent -match "/pmu/partant/{id}/performance" -and
    $readmeContent -match "/pmu/partant/{id}/skill-category") {
    Write-Host "✓ All endpoints documented in README.md" -ForegroundColor Green
} else {
    Write-Host "⚠️ Some endpoints may be missing from README.md" -ForegroundColor Yellow
}

# Check if GUIDE.md contains all endpoints
Write-Host -NoNewline "Checking GUIDE.md documentation... "
$guideContent = Get-Content "GUIDE.md" -Raw
if ($guideContent -match "/pmu/course/{id}/betting-eligible" -and 
    $guideContent -match "/pmu/course/{id}/difficulty" -and
    $guideContent -match "/pmu/partant/{id}/good-standing" -and
    $guideContent -match "/pmu/partant/{id}/performance" -and
    $guideContent -match "/pmu/partant/{id}/skill-category") {
    Write-Host "✓ All endpoints documented in GUIDE.md" -ForegroundColor Green
} else {
    Write-Host "⚠️ Some endpoints may be missing from GUIDE.md" -ForegroundColor Yellow
}

# Check if controllers have proper OpenAPI annotations
Write-Host -NoNewline "Checking OpenAPI annotations in controllers... "
$courseController = Get-Content "src/main/java/com/pmu2/exec/infrastrure/rest/PmuCourseController.java" -Raw
$partantController = Get-Content "src/main/java/com/pmu2/exec/infrastrure/rest/PmuPartantController.java" -Raw
if ($courseController -match "@Operation" -and $partantController -match "@Operation") {
    Write-Host "✓ Controllers have OpenAPI annotations" -ForegroundColor Green
} else {
    Write-Host "⚠️ Controllers may be missing OpenAPI annotations" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "📊 Summary:" -ForegroundColor Cyan
Write-Host "===========" -ForegroundColor Cyan

if ($swaggerOk) {
    Write-Host "✓ Swagger/OpenAPI documentation is accessible" -ForegroundColor Green
} else {
    Write-Host "✗ Swagger/OpenAPI documentation is not accessible" -ForegroundColor Red
}

Write-Host ""
Write-Host "🎯 Next Steps:" -ForegroundColor Cyan
Write-Host "-------------" -ForegroundColor Cyan
Write-Host "1. Visit Swagger UI: $BaseUrl/swagger-ui/index.html"
Write-Host "2. Check OpenAPI spec: $BaseUrl/v3/api-docs"
Write-Host "3. Test endpoints interactively in Swagger UI"
Write-Host "4. Review API examples in README.md and GUIDE.md"

Write-Host ""
Write-Host "✅ API Documentation Validation Complete!" -ForegroundColor Green
