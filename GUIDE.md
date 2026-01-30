# PMU Exec Application Guide

## 📋 Table of Contents

1. [Getting Started](#getting-started)
2. [Architecture Overview](#architecture-overview)
3. [Development Setup](#development-setup)
4. [API Documentation](#api-documentation)
5. [Business Logic & Domain Services](#business-logic--domain-services)
6. [Validation & Error Handling](#validation--error-handling)
7. [Testing](#testing)
8. [Troubleshooting](#troubleshooting)

## 🚀 Getting Started

### Prerequisites
- **Java 21** or higher
- **Maven 3.6** or higher
- **Docker** and **Docker Compose**

### Quick Start

1. **Start Infrastructure Services**
   ```bash
   docker-compose up -d
   ```

2. **Configure Database**
   ```sql
   CREATE DATABASE mydb;
   CREATE USER 'mkyong'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON mydb.* TO 'mkyong'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Run Application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access API Documentation**
   - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
   - Kafdrop (Kafka UI): `http://localhost:8085`

## 🏗️ Architecture Overview

### Clean Architecture Implementation

This application follows **Clean Architecture** principles with strict adherence to **SOLID** principles:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐                 │
│  │ REST Controllers│  │  Event Publishers│                 │
│  └─────────────────┘  └─────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                         │
│  ┌─────────────────┐  ┌─────────────────┐                 │
│  │   Services     │  │   Validators     │                 │
│  └─────────────────┘  └─────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐                 │
│  │ Domain Services │  │  Domain Models  │                 │
│  └─────────────────┘  └─────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                 Infrastructure Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐                 │
│  │   Repositories  │  │   Kafka Config  │                 │
│  └─────────────────┘  └─────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

#### Domain Layer
- **`CourseRecord`** & **`PartantRecord`** - Immutable domain models
- **`CourseDomainService`** - Pure business logic for courses
- **`PartantDomainService`** - Pure business logic for partants

#### Application Layer
- **`PmuCourseService`** - Course management orchestration
- **`PmuPartantService`** - Partant management orchestration
- **`CourseEventPublisher`** - Kafka event publishing
- **`CourseValidator`** & **`PartantValidator`** - Data validation

#### Infrastructure Layer
- **JPA Entities & Repositories** - Database persistence
- **Kafka Components** - Message streaming
- **REST Controllers** - HTTP API endpoints

## 🔧 Development Setup

### Project Structure

```
src/main/java/com/pmu2/exec/
├── ExecApplication.java              # Main application entry point
├── config/
│   └── SwaggerConfig.java           # OpenAPI configuration
├── domain/
│   ├── CourseRecord.java             # Course domain model
│   ├── PartantRecord.java            # Partant domain model
│   └── service/
│       ├── CourseDomainService.java  # Course business logic
│       └── PartantDomainService.java # Partant business logic
├── infrastructure/
│   ├── db/sql/                       # Database entities
│   ├── kafka/                        # Kafka configuration
│   ├── repository/                   # JPA repositories
│   └── rest/                         # REST controllers
├── service/
│   ├── PmuCourseService.java         # Course orchestration
│   ├── PmuPartantService.java        # Partant orchestration
│   ├── CourseEventPublisher.java     # Event publishing
│   ├── CoursePartantService.java     # Relationship management
│   └── mapper/                       # MapStruct mappers
├── validation/
│   ├── CourseValidator.java          # Course validation
│   └── PartantValidator.java         # Partant validation
└── exception/
    ├── CourseNotFoundException.java  # Course-specific exceptions
    ├── PartantNotFoundException.java # Partant-specific exceptions
    ├── CourseBusinessException.java  # Course business rule violations
    └── PartantBusinessException.java # Partant business rule violations
```

### Environment Configuration

#### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3308/mydb
spring.datasource.username=mkyong
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.topic.name=pmu-events

# API Documentation
pmu-exec.openapi.dev-url=http://localhost:8080
```

## 📚 API Documentation

### Course Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/pmu/course` | Retrieve all courses |
| POST | `/pmu/course` | Create a new course |
| DELETE | `/pmu/course/{id}` | Delete a course |
| GET | `/pmu/course/find/{name}` | Find courses by name |
| GET | `/pmu/course/{id}/betting-eligible` | Check betting eligibility |
| GET | `/pmu/course/{id}/difficulty` | Get course difficulty |

### Partant Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/pmu/partant` | Retrieve all partants |
| POST | `/pmu/partant` | Create a new partant |
| DELETE | `/pmu/partant/{id}` | Delete a partant |
| GET | `/pmu/partant/find/{name}` | Find partants by name |
| GET | `/pmu/partant/{id}/good-standing` | Check good standing |
| GET | `/pmu/partant/{id}/performance` | Get performance score |
| GET | `/pmu/partant/{id}/skill-category` | Get skill category |

### Request/Response Examples

#### Create Course
```json
POST /pmu/course
{
  "name": "Grand Prix de Paris",
  "number": 42,
  "date": "2024-06-15",
  "partants": [
    {
      "id": 1,
      "name": "Thunder Bolt",
      "number": 1
    },
    {
      "id": 2,
      "name": "Lightning Strike",
      "number": 2
    }
  ]
}
```

#### Create Partant
```json
POST /pmu/partant
{
  "id": 1,
  "name": "Thunder Bolt",
  "number": 1
}
```

#### Check Betting Eligibility
```bash
GET /pmu/course/1/betting-eligible
Response: true
```

#### Get Course Difficulty
```bash
GET /pmu/course/1/difficulty
Response: 7
```

#### Check Partant Good Standing
```bash
GET /pmu/partant/1/good-standing
Response: true
```

#### Get Partant Performance Score
```bash
GET /pmu/partant/1/performance
Response: 85
```

#### Get Partant Skill Category
```bash
GET /pmu/partant/1/skill-category
Response: "EXPERT"
```

## 🧠 Business Logic & Domain Services

### Course Domain Service

The `CourseDomainService` implements pure business rules for courses:

#### Course Creation Validation
- **Date Restrictions**: Course cannot be more than 6 months in future
- **Partant Count**: Must have 3-20 partants
- **Unique Numbers**: All partant numbers must be unique and sequential (1, 2, 3...)

#### Betting Eligibility
- **Time Requirement**: Course must be at least 24 hours in future
- **Partant Range**: 5-15 partants for betting eligibility

#### Difficulty Calculation
- **Base Score**: 5 (medium difficulty)
- **Partant Count Factor**: More partants = higher difficulty
- **Course Number Factor**: Higher numbers = higher difficulty

### Partant Domain Service

The `PartantDomainService` implements pure business rules for partants:

#### Eligibility Validation
- **Name Requirements**: 2-50 characters, not empty
- **Number Range**: 1-99, positive integer

#### Performance Scoring
- **Base Score**: 50 points
- **Number Factor**: Lower numbers = higher score
- **Name Length Factor**: Shorter names = higher score
- **Good Standing**: Must be in good standing for full score

#### Skill Categories
- **EXPERT**: 85-100 points
- **ADVANCED**: 70-84 points
- **INTERMEDIATE**: 50-69 points
- **NOVICE**: 1-49 points

## ✅ Validation & Error Handling

### Validation Layers

#### 1. Data Validation (Validation Components)
```java
// Example: Course data validation
courseValidator.validateCourseRecord(course);  // Format, null checks
courseValidator.validateCourseExistsById(id); // Existence checks
```

#### 2. Business Rule Validation (Domain Services)
```java
// Example: Course business rules
courseDomainService.validateCourseCreation(course);  // Business logic
courseDomainService.isEligibleForBetting(course);    // Business rules
```

### Exception Hierarchy

```
RuntimeException
├── CourseNotFoundException     # Course not found
├── PartantNotFoundException   # Partant not found
├── CourseBusinessException    # Course business rule violations
└── PartantBusinessException   # Partant business rule violations
```

### Error Response Format
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Course must have at least 3 partants. Current count: 2",
  "path": "/pmu/course"
}
```

## 🧪 Testing

### Current Test Status
- **Total Tests**: 230 tests passing
- **Unit Tests**: 226 tests passing (domain, service, validation, mapper tests)
- **Integration Tests**: 4 Kafka tests passing (producer and consumer tests)
- **Failures**: 0
- **Errors**: 0
- **Skipped**: 2 (Docker-dependent integration tests)

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CourseDomainServiceTest

# Run only Kafka integration tests
./mvnw test -Dtest=CourseProducerTest,CourseConsumerTest

# Run with coverage
./mvnw test jacoco:report

# Run specific test method
./mvnw test -Dtest=PartantDomainServiceTest#shouldThrowExceptionWhenNumberIsNotPositive
```

### Test Structure

#### Unit Tests (Domain Services)
```java
@Test
void shouldValidateCourseCreation() {
    // Pure unit tests - no Spring context needed
    CourseRecord course = new CourseRecord(...);
    assertDoesNotThrow(() -> courseDomainService.validateCourseCreation(course));
}
```

#### Kafka Integration Tests
```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
@DirtiesContext
class CourseProducerTest {
    // Tests with embedded Kafka for producer/consumer validation
}
```

#### Testing Validation Constraints

When testing domain service validation with Jakarta Bean Validation constraints, use reflection to bypass validation:

```java
@Test
void shouldThrowExceptionWhenNumberIsNotPositive() {
    // Given - use reflection to create PartantRecord with invalid number
    try {
        java.lang.reflect.Constructor<PartantRecord> constructor = 
            PartantRecord.class.getDeclaredConstructor(Integer.class, String.class, int.class);
        constructor.setAccessible(true);
        PartantRecord partant = constructor.newInstance(1, "Thunder Bolt", 0);

        // When & Then
        BusinessValidationException exception = assertThrows(BusinessValidationException.class,
            () -> partantDomainService.validatePartantEligibility(partant));
        assertTrue(exception.getMessage().contains("Partant number must be between 1 and 99"));
    } catch (Exception e) {
        fail("Failed to create test PartantRecord: " + e.getMessage());
    }
}
```

**Key Points:**
- Use reflection to bypass Jakarta Bean Validation when testing domain service validation
- Wrap reflection code in try-catch blocks for proper error handling
- Test domain validation logic independently of framework validation
- Ensure test failures are meaningful with descriptive error messages

#### Integration Tests (Application Services)
```java
@SpringBootTest
@Testcontainers
class PmuCourseServiceIntegrationTest {
    // Full integration tests with database and Kafka
}
```

### Test Coverage Areas

1. **Domain Services**: Business logic validation
2. **Validation Components**: Data integrity checks
3. **Application Services**: Orchestration logic
4. **Repositories**: Data access layer
5. **Controllers**: API endpoints
6. **Kafka Integration**: Event publishing/consuming with EmbeddedKafka
   - Producer tests: `CourseProducerTest` (2 tests)
   - Consumer tests: `CourseConsumerTest` (2 tests)
   - Template configuration: Proper KafkaTemplate generics and @Primary annotation

## 🔍 Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check MySQL container
docker ps | grep mysql

# Check container logs
docker logs <mysql-container-id>
```

#### 2. Kafka Connection Issues
```bash
# Check Kafka container
docker ps | grep kafka

# Check Kafka topics
docker exec -it <kafka-container-id> kafka-topics.sh --list --bootstrap-server localhost:9092
```

#### 3. Application Startup Issues
```bash
# Check application logs
./mvnw spring-boot:run

# Check Flyway migrations
./mvnw flyway:info
./mvnw flyway:migrate
```

#### 4. Test Validation Issues
```bash
# Check specific test failures
./mvnw test -Dtest=PartantDomainServiceTest -X

# Run tests with debug output
./mvnw test -Dmaven.test.debug=true

# Check test compilation
./mvnw test-compile
```

**Common Test Issues:**
- **Jakarta Validation Errors**: Use reflection to bypass validation in unit tests
- **Constructor Access**: Set `setAccessible(true)` on private constructors
- **Test Isolation**: Ensure tests don't share state or dependencies
- **Mock Configuration**: Verify mocks are properly configured for domain services

### Debug Mode

Enable debug logging in `application.properties`:
```properties
logging.level.com.pmu2.exec=DEBUG
logging.level.org.springframework.kafka=DEBUG
```

### Health Checks

- **Application Health**: `http://localhost:8080/actuator/health`
- **MySQL Health**: Check container status
- **Kafka Health**: `http://localhost:8085` (Kafdrop)

## 📖 Additional Resources

### Reference Documentation
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring for Apache Kafka](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- [TestContainers](https://www.testcontainers.org/)

### Best Practices
- **SOLID Principles**: Implemented throughout the codebase
- **Clean Architecture**: Clear separation of concerns
- **Domain-Driven Design**: Business logic in domain services
- **Test-Driven Development**: Comprehensive test coverage
- **Event-Driven Architecture**: Kafka for asynchronous communication

## 🤝 Contributing

1. Follow the existing code structure and patterns
2. Write unit tests for new domain logic
3. Update API documentation for new endpoints
4. Ensure all validation layers are properly implemented
5. Test with Docker Compose setup

## 📞 Support

For technical support:
- Check the troubleshooting section first
- Review application logs for detailed error messages
- Verify Docker containers are running properly
- Test API endpoints using Swagger UI
