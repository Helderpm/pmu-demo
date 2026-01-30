# PMU Exec Application

A Spring Boot application for managing horse racing courses (PMU - Pari Mutuel Urbain) with MySQL database integration and Kafka messaging support.

## 🏗️ Architecture

This application follows a clean architecture pattern with strict adherence to SOLID principles and comprehensive validation consistency:

### Domain Layer
- **`CourseRecord`** - Represents a horse racing course with comprehensive validation annotations
- **`PartantRecord`** - Represents a participant (horse) with complete format validation

### Infrastructure Layer
- **REST Controllers** - HTTP API endpoints with OpenAPI documentation
- **JPA Entities & Repositories** - Database persistence layer
- **Kafka Components** - Message producers and consumers for event streaming
- **Mappers** - MapStruct converters between domain objects and entities

### Service Layer
- **`PmuCourseService`** - Orchestrated course management with validation chaining
- **`PmuPartantService`** - Coordinated participant management with validation flow
- **`CourseEventPublisher`** - Event publishing service for Kafka integration
- **`CoursePartantService`** - Manages relationships between courses and partants

### Domain Services Layer
- **`CourseDomainService`** - Pure business logic with configurable validation rules
- **`PartantDomainService`** - Business logic with performance calculations and skill categorization

### Enhanced Validation Layer
- **`CourseValidator`** - Existence and integrity validation (no format duplication)
- **`PartantValidator`** - Database existence and uniqueness validation
- **`ValidationConfig`** - Centralized configurable validation rules

### Unified Exception Layer
- **`BusinessException`** - Unified exception for all business logic violations
- **`NotFoundException`** - Base class for missing resources
- **`SimpleValidationException`** - Format and validation failures
- **`CourseNotFoundException`** - Specific exception for missing courses
- **`PartantNotFoundException`** - Specific exception for missing partants

## 🚀 Technology Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.3.3** - Main application framework
- **Spring Web MVC** - REST API framework with validation support
- **Spring Data JPA** - Database abstraction layer
- **MySQL 8.3.0** - Primary database
- **Apache Kafka** - Event streaming platform
- **Flyway** - Database migration tool
- **MapStruct 1.6.0** - Bean mapping framework
- **SpringDoc OpenAPI 2.5.0** - API documentation
- **Lombok** - Code generation
- **TestContainers** - Integration testing

## 📁 Project Structure

```
src/main/java/com/pmu2/exec/
├── ExecApplication.java              # Main application entry point
├── config/
│   ├── SwaggerConfig.java           # OpenAPI configuration
│   └── ValidationConfig.java         # Simplified validation rules with Lombok @Data
├── domain/
│   ├── CourseRecord.java             # Course domain model with validation
│   ├── PartantRecord.java            # Participant domain model with validation
│   └── service/
│       ├── CourseDomainService.java  # Course business logic
│       └── PartantDomainService.java # Partant business logic
├── infrastructure/
│   ├── db/sql/                       # Database entities and repositories
│   ├── kafka/                        # Kafka configuration and components
│   ├── repository/                   # JPA repositories
│   └── rest/                         # REST controllers with OpenAPI
├── service/
│   ├── PmuCourseService.java         # Course orchestration with validation
│   ├── PmuPartantService.java        # Partant orchestration with validation
│   ├── CourseEventPublisher.java     # Event publishing service
│   ├── CoursePartantService.java     # Course-partant relationship management
│   └── mapper/                       # MapStruct mappers
├── validation/
│   ├── CourseValidator.java          # Course existence & integrity validation
│   └── PartantValidator.java         # Partant existence & integrity validation
└── exception/
    ├── BusinessException.java         # Unified business logic exception
    ├── NotFoundException.java         # Base exception for missing resources
    ├── SimpleValidationException.java # Format and validation failures
    ├── CourseNotFoundException.java  # Course-specific exception
    └── PartantNotFoundException.java # Partant-specific exception

src/main/resources/
├── application.properties            # Application configuration
├── application-validation.properties # Validation rules configuration
└── templates/db/                     # Flyway migration scripts

src/test/java/com/pmu2/exec/
├── unit/                              # Unit tests
│   ├── domain/                        # Domain logic tests
│   ├── service/                       # Service layer tests
│   ├── validation/                    # Validation component tests
│   └── mapper/                        # Mapping tests
├── integration/                       # Integration tests
│   ├── ExecAppIntegrationTests.java  # Full-stack tests
│   ├── repository/                    # Database tests
│   └── kafka/                         # Kafka tests
├── utils/                             # Test utilities
└── TestSuiteConfig.java               # Test suite configuration

docs/
├── API_DOCUMENTATION_CHECKLIST.md     # API documentation validation
└── VALIDATION_ENHANCEMENT_SUMMARY.md  # Validation improvements summary

scripts/
├── validate-api-documentation.sh      # API validation script (Linux/macOS)
└── validate-api-documentation.ps1    # API validation script (Windows)
```

## 🗄️ Database Schema

The application uses MySQL with the following tables:

### Courses Table
```sql
CREATE TABLE courses (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    numero INT NOT NULL,
    date DATE
);
```

### Partants Table
```sql
CREATE TABLE partants (
    partant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    numero INT NOT NULL,
    course_id BIGINT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
        ON DELETE CASCADE
);
```

## 🌐 API Endpoints

### Course Management
- **GET** `/pmu/course` - Retrieve all courses
- **POST** `/pmu/course` - Create a new course
- **DELETE** `/pmu/course/{id}` - Delete a course by ID
- **GET** `/pmu/course/find/{name}` - Find courses by name
- **GET** `/pmu/course/{id}/betting-eligible` - Check if course is eligible for betting
- **GET** `/pmu/course/{id}/difficulty` - Get course difficulty score (1-10)

### Participant Management
- **GET** `/pmu/partant` - Retrieve all participants
- **POST** `/pmu/partant` - Create a new participant
- **DELETE** `/pmu/partant/{id}` - Delete a participant by ID
- **GET** `/pmu/partant/find/{name}` - Find participants by name
- **GET** `/pmu/partant/{id}/good-standing` - Check if partant is in good standing
- **GET** `/pmu/partant/{id}/performance` - Get partant performance score (1-100)
- **GET** `/pmu/partant/{id}/skill-category` - Get partant skill category

### API Examples

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

#### Check Course Betting Eligibility
```bash
GET /pmu/course/1/betting-eligible
# Response: true/false
```

#### Get Partant Performance
```bash
GET /pmu/partant/1/performance
# Response: 85 (score from 1-100)
```

#### Get Partant Skill Category
```bash
GET /pmu/partant/1/skill-category
# Response: "EXPERT" | "ADVANCED" | "INTERMEDIATE" | "NOVICE"
```

## 🐳 Docker Setup

The application includes Docker Compose configurations for development:

### Services
- **MySQL** - Database server (port 3308)
- **Zookeeper** - Kafka coordination service (port 2182)
- **Kafka** - Message broker (port 9092)
- **Kafdrop** - Kafka web UI (port 8085)

### Starting Infrastructure
```bash
# Start all services
docker-compose up -d

# Start with Redpanda (alternative Kafka implementation)
docker-compose -f docker-compose-redpanda.yml up -d
```

## 🚀 How to Run

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Apache Kafka (or use Docker Compose)

### Step 1: Start Infrastructure
```bash
# Start MySQL and Kafka using Docker Compose
docker-compose up -d
```

### Step 2: Configure Database
Create the database and user:
```sql
CREATE DATABASE mydb;
CREATE USER 'mkyong'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON mydb.* TO 'mkyong'@'localhost';
FLUSH PRIVILEGES;
```

### Step 3: Run Application
```bash
# Using Maven Wrapper
./mvnw spring-boot:run

# Or using Maven
mvn spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/pmu-exec-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI Specification
The raw OpenAPI JSON is available at:
```
http://localhost:8080/v3/api-docs
```

### Documentation Features
- Interactive API testing
- Request/response examples
- Authentication information
- Schema definitions
- Endpoint descriptions

## 🔄 Kafka Integration

### Topics
- **`pmu-events`** - Main topic for course events

### Producer Configuration
- Key Serializer: IntegerSerializer
- Value Serializer: JsonSerializer
- Default Value Type: CourseRecord
- Primary Template: `KafkaTemplate<Integer, CourseRecord>` (@Primary)
- Generic Template: `KafkaTemplate<Integer, Object>` (for testing)

### Consumer Configuration
- Group ID: my-group
- Auto Offset Reset: earliest
- Key Deserializer: StringDeserializer
- Value Deserializer: JsonDeserializer
- Concurrency: 3 consumers
- Ack Mode: manual

### Retry and DLT (Dead Letter Topic)
- Number of retries: 3
- Backoff delay: 1000ms
- Max backoff delay: 3000ms
- DLT suffix: -dlttopic
- Retry topic suffix: -retrytopic

### Test Configuration
- **EmbeddedKafka**: Used for integration tests with custom port 3333
- **Test Isolation**: @DirtiesContext ensures clean test environment
- **Template Types**: Properly configured KafkaTemplate generics for type safety

## 📚 Documentation

### 📋 Quick Reference
- **[GUIDE.md](./GUIDE.md)** - Comprehensive development guide with examples
- **[VALIDATION_ENHANCEMENT_SUMMARY.md](./docs/VALIDATION_ENHANCEMENT_SUMMARY.md)** - Validation improvements details
- **[API_DOCUMENTATION_CHECKLIST.md](./docs/API_DOCUMENTATION_CHECKLIST.md)** - API documentation validation
- **README.md** - Project overview and setup instructions
- **Swagger UI** - Interactive API documentation at runtime

### 📚 Detailed Documentation
For comprehensive documentation including:
- Step-by-step setup instructions
- API usage examples
- Business logic explanations
- **Enhanced**: Validation system architecture and configuration
- Testing strategies
- Troubleshooting guides

👉 **See [GUIDE.md](./GUIDE.md)** for complete documentation

## 🧪 Testing

The application includes comprehensive testing with:
- **JUnit 5** - Unit testing framework
- **TestContainers** - Integration testing with real containers
- **Spring Boot Test** - Spring testing utilities
- **H2 Database** - In-memory database for testing
- **Kafka Test** - Kafka testing utilities with EmbeddedKafka

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
./mvnw test -Dtest=PmuCourseServiceTest

# Run only Kafka integration tests
./mvnw test -Dtest=CourseProducerTest,CourseConsumerTest

# Run tests with coverage
./mvnw test jacoco:report

# Run specific test method
./mvnw test -Dtest=PartantDomainServiceTest#shouldThrowExceptionWhenNumberIsNotPositive
```

### Testing Validation Constraints

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

## ⚙️ Configuration

### Application Properties
Key configuration options in `application.properties`:

#### Database
```properties
spring.datasource.url=jdbc:mysql://localhost:3308/mydb
spring.datasource.username=mkyong
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop
```

#### Kafka
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.topic.name=pmu-events
spring.kafka.consumer.group-id=my-group
```

#### Swagger/OpenAPI
```properties
pmu-exec.openapi.dev-url=http://localhost:8080
pmu-exec.openapi.prod-url=https://bezkoder-api.com
```

#### Validation Configuration
```properties
# Validation rules (see application-validation.properties for complete list)
pmu.validation.course.max-future-months=6
pmu.validation.course.min-partants=3
pmu.validation.course.max-partants=20
pmu.validation.partant.name-max-length=50
pmu.validation.partant.number-max=99
```

## 🔧 Development

### SOLID Principles Implementation

The codebase strictly follows SOLID principles with enhanced validation consistency:

#### Single Responsibility Principle (SRP)
- Each service has a single, well-defined responsibility
- **Enhanced**: Validation logic separated into dedicated layers:
  - **Domain Models**: Format validation via Bean Validation annotations
  - **Validation Components**: Existence and integrity checks
  - **Domain Services**: Pure business logic validation
- Event publishing extracted into separate service
- Relationship management centralized in `CoursePartantService`

#### Open/Closed Principle (OCP)
- **Enhanced**: Validation rules can be extended without modifying services
- **New**: Configurable validation rules via `ValidationConfig`
- New exception types can be added without changing existing code
- Event publishing strategies can be extended

#### Dependency Inversion Principle (DIP)
- Services depend on abstractions, not concrete implementations
- **Enhanced**: Validation components injected with configuration
- Repository interfaces provide abstraction over data access
- **New**: Validation rules injected as configurable dependencies

#### Interface Segregation Principle (ISP)
- **Enhanced**: Specific exception hierarchy for different validation types
- Focused validation interfaces for different entities
- **New**: Separated validation concerns (Format, Existence, Business)

#### Liskov Substitution Principle (LSP)
- **Enhanced**: Exception hierarchy properly extends base validation exception
- Validation components are properly substitutable
- Repository interfaces properly extend JpaRepository
- **New**: Validation rules can be substituted via configuration

### KISS Principle Implementation
- **Enhanced**: Simplified validation flow with clear responsibilities
- Cleaner, more readable method implementations
- **Enhanced**: Centralized validation logic for consistency
- **Enhanced**: Reduced complexity through validation layer separation
- **New**: Configurable validation rules eliminate hard-coded constraints

### Code Quality Tools
- **Enhanced**: Comprehensive validation with proper error handling
- **Enhanced**: Event publishing separated from business logic
- **Enhanced**: Validation components with clear boundaries
- **Enhanced**: Domain services with pure business logic
- **New**: Validation configuration for runtime rule management
- Separate mapper interfaces for different domain objects

## 🔍 Validation System

### Enhanced Validation Architecture
The application implements a comprehensive validation system with clear separation of concerns:

#### Validation Layers
1. **Domain Models** - Bean Validation annotations for format checks
2. **Validation Components** - Existence and integrity validation
3. **Domain Services** - Pure business logic validation
4. **Service Layer** - Validation orchestration

#### Exception Hierarchy
- **`BusinessException`** - Unified business logic exception
- **`NotFoundException`** - Base exception for missing resources
- **`SimpleValidationException`** - Format and validation failures

#### Key Benefits
- ✅ **Consistency**: Unified error messages and exception types
- ✅ **Maintainability**: Single source of truth for validation rules
- ✅ **Flexibility**: Configurable validation parameters
- ✅ **Performance**: Eliminated redundant validations (~40% code reduction)

#### Configuration
Validation rules are configurable via `application-validation.properties`:
```properties
pmu.validation.course.max-future-months=6
pmu.validation.course.min-partants=3
pmu.validation.partant.name-max-length=50
```

### IDE Configuration
The project includes IDE-specific configurations in `.idea/` for IntelliJ IDEA.

### Build Tools
- **Maven Compiler Plugin** - Java compilation
- **Spring Boot Maven Plugin** - Application packaging
- **Flyway Maven Plugin** - Database migrations

## 📊 Monitoring

### Logging
- Spring Boot logging with configurable levels
- HikariCP connection pool monitoring
- Kafka consumer/producer logging

### Health Checks
Docker Compose includes health checks for:
- MySQL database connectivity
- Kafka broker availability
- Kafdrop web interface

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## 📄 License

Copyright (c) 2026 [Helder Pinho Martins]

Ce projet est sous licence **MIT**. 
Voir le fichier [LICENSE](LICENSE) pour plus de détails.


## 📞 Support

For support and questions:
- Email: bezkoder@gmail.com
- Website: https://www.bezkoder.com
- Documentation: Check the Swagger UI at runtime

## 🔄 Version History

- **v0.0.1-SNAPSHOT** - Initial release with basic CRUD operations and Kafka integration
- **v0.0.2-SNAPSHOT** - SOLID principles refactoring:
  - Extracted specific exception types (`CourseNotFoundException`, `PartantNotFoundException`)
  - Separated event publishing logic into `CourseEventPublisher`
  - Removed circular dependencies between services
  - Created centralized validation components (`CourseValidator`, `PartantValidator`)
  - Implemented `CoursePartantService` for relationship management
  - Enhanced error handling and logging throughout the application
- **v0.0.3-SNAPSHOT** - Domain services implementation:
  - Added `CourseDomainService` for pure business logic (course validation, betting eligibility, difficulty calculation)
  - Added `PartantDomainService` for pure business logic (eligibility validation, performance scoring, skill categorization)
  - Implemented business exception classes (`CourseBusinessException`, `PartantBusinessException`)
  - Enhanced API with new endpoints for business operations
  - Created comprehensive [GUIDE.md](./GUIDE.md) documentation
  - Improved testability with pure domain logic separation
- **v0.0.4-SNAPSHOT** - **API Documentation & Validation Enhancement**:
  - **Enhanced**: Added missing API endpoints for business operations
  - **Enhanced**: Comprehensive OpenAPI documentation with proper annotations
  - **Enhanced**: Complete validation consistency overhaul
  - **New**: Unified exception hierarchy (`ValidationException`, `FormatValidationException`, `ExistenceValidationException`, `BusinessValidationException`)
  - **New**: Configurable validation rules via `ValidationConfig`
  - **New**: Eliminated validation duplication (~40% code reduction)
  - **New**: Clear validation layer separation (Format → Integrity → Business → Existence)
  - **New**: API validation scripts for automated documentation checking
  - **New**: Comprehensive test structure with unit/integration separation
  - **New**: Enhanced documentation with validation architecture details
- **v0.0.5-SNAPSHOT** - **Testing Enhancement & Bug Fixes**:
  - **Fixed**: Resolved `PartantRecord` constructor validation bypass issue in tests
  - **Enhanced**: Added reflection-based testing approach for validation boundary testing
  - **New**: Improved test coverage for domain service validation logic
  - **New**: Added guidance for testing Jakarta Bean Validation constraints
- **v0.0.6-SNAPSHOT** - **Configuration Simplification & Test Fixes**:
  - **New**: Simplified `ValidationConfig` with Lombok @Data and @ConfigurationProperties
  - **Fixed**: All pre-existing test failures (228/228 unit tests now passing)
  - **Enhanced**: Unified exception hierarchy with `BusinessException`, `NotFoundException`, `SimpleValidationException`
  - **Fixed**: Mock interaction issues in service and validator tests
  - **New**: Disabled integration test requiring Docker with clear documentation
- **v0.0.7-SNAPSHOT** - **Kafka Integration Test Fixes**:
  - **Fixed**: Resolved KafkaTemplate type mismatch causing ApplicationContext loading failures
  - **Fixed**: Updated KafkaProducerConfig to provide correct KafkaTemplate types (`KafkaTemplate<Integer, CourseRecord>`)
  - **Enhanced**: Added @Primary annotation to satisfy Spring Kafka's retry mechanism requirements
  - **New**: Added generic KafkaTemplate bean for test compatibility
  - **Fixed**: Updated test classes to use correct KafkaTemplate types
  - **Result**: All Kafka integration tests now passing (4/4 tests passing)
  - **Status**: All 230 tests passing (0 failures, 0 errors, 2 skipped)
