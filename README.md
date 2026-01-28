# PMU Exec Application

A Spring Boot application for managing horse racing courses (PMU - Pari Mutuel Urbain) with MySQL database integration and Kafka messaging support.

## 🏗️ Architecture

This application follows a clean architecture pattern with strict adherence to SOLID principles:

### Domain Layer
- **`CourseRecord`** - Represents a horse racing course with participants
- **`PartantRecord`** - Represents a participant (horse) in a race

### Infrastructure Layer
- **REST Controllers** - HTTP API endpoints for courses and participants
- **JPA Entities & Repositories** - Database persistence layer
- **Kafka Components** - Message producers and consumers for event streaming
- **Mappers** - MapStruct converters between domain objects and entities

### Service Layer
- **`PmuCourseService`** - Business logic for course management
- **`PmuPartantService`** - Business logic for participant management
- **`CourseEventPublisher`** - Event publishing service for Kafka integration
- **`CoursePartantService`** - Manages relationships between courses and partants

### Domain Services Layer
- **`CourseDomainService`** - Pure business logic for course operations
- **`PartantDomainService`** - Pure business logic for partant operations

### Validation Layer
- **`CourseValidator`** - Centralized course validation logic
- **`PartantValidator`** - Centralized partant validation logic

### Exception Layer
- **`CourseNotFoundException`** - Specific exception for missing courses
- **`PartantNotFoundException`** - Specific exception for missing partants

## 🚀 Technology Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.3.3** - Main application framework
- **Spring Web MVC** - REST API framework
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
│   └── SwaggerConfig.java           # OpenAPI configuration
├── domain/
│   ├── CourseRecord.java             # Course domain model
│   ├── PartantRecord.java            # Participant domain model
│   └── service/
│       ├── CourseDomainService.java  # Course business logic
│       └── PartantDomainService.java # Partant business logic
├── infrastructure/
│   ├── dao/                          # JPA entities for beneficiaries
│   ├── db/sql/                       # Database entities and repositories
│   ├── kafka/                        # Kafka configuration and components
│   ├── repository/                   # JPA repositories
│   └── rest/                         # REST controllers
├── service/
│   ├── PmuCourseService.java         # Course business logic
│   ├── PmuPartantService.java        # Participant business logic
│   ├── CourseEventPublisher.java     # Event publishing service
│   ├── CoursePartantService.java     # Course-partant relationship management
│   └── mapper/                       # MapStruct mappers
├── validation/
│   ├── CourseValidator.java          # Course validation logic
│   └── PartantValidator.java         # Partant validation logic
└── exception/
    ├── CourseNotFoundException.java  # Course-specific exception
    ├── PartantNotFoundException.java # Partant-specific exception
    ├── CourseBusinessException.java  # Course business rule violations
    ├── PartantBusinessException.java # Partant business rule violations
    └── AException.java               # Legacy exception (deprecated)

src/main/resources/
├── application.properties            # Application configuration
└── templates/db/                     # Flyway migration scripts
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
- Key Serializer: StringSerializer
- Value Serializer: JsonSerializer
- Default Value Type: CourseRecord

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

## 📖 Documentation

### 📋 Quick Reference
- **[GUIDE.md](./GUIDE.md)** - Comprehensive development guide with examples
- **README.md** - Project overview and setup instructions
- **Swagger UI** - Interactive API documentation at runtime

### 📚 Detailed Documentation
For comprehensive documentation including:
- Step-by-step setup instructions
- API usage examples
- Business logic explanations
- Testing strategies
- Troubleshooting guides

👉 **See [GUIDE.md](./GUIDE.md)** for complete documentation

## 🧪 Testing

The application includes comprehensive testing with:
- **JUnit 5** - Unit testing framework
- **TestContainers** - Integration testing with real containers
- **Spring Boot Test** - Spring testing utilities
- **H2 Database** - In-memory database for testing
- **Kafka Test** - Kafka testing utilities

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=PmuCourseServiceTest

# Run tests with coverage
./mvnw test jacoco:report
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

## 🔧 Development

### SOLID Principles Implementation

The codebase strictly follows SOLID principles:

#### Single Responsibility Principle (SRP)
- Each service has a single, well-defined responsibility
- Validation logic separated into dedicated validator components
- Event publishing extracted into separate service
- Relationship management centralized in `CoursePartantService`

#### Open/Closed Principle (OCP)
- Validation rules can be extended without modifying services
- New exception types can be added without changing existing code
- Event publishing strategies can be extended

#### Dependency Inversion Principle (DIP)
- Services depend on abstractions, not concrete implementations
- Validation components are injected as dependencies
- Repository interfaces provide abstraction over data access

#### Interface Segregation Principle (ISP)
- Specific exception types for different error scenarios
- Focused validation interfaces for different entities
- Separate mapper interfaces for different domain objects

#### Liskov Substitution Principle (LSP)
- Exception types properly extend RuntimeException
- Repository interfaces properly extend JpaRepository
- Validator components are properly substitutable

### KISS Principle Implementation
- Simplified service dependencies by removing circular references
- Cleaner, more readable method implementations
- Centralized validation logic for consistency
- Reduced complexity in business operations

### Code Quality Tools
- **Lombok** - Reduces boilerplate code
- **MapStruct** - Type-safe bean mapping
- **Flyway** - Database version control
- **Validation Components** - Centralized business rule validation
- **Event Publishing** - Separated from business logic

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
