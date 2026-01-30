# PMU Exec Application - Getting Started Guide

## 🚀 Quick Start

### Prerequisites
- **Java 21** or higher
- **Maven 3.6** or higher  
- **Docker** and **Docker Compose**

### Step 1: Start Infrastructure Services
```bash
docker-compose up -d
```

This starts:
- **MySQL** database (port 3308)
- **Zookeeper** (port 2182)
- **Kafka** message broker (port 9092)
- **Kafdrop** Kafka web UI (port 8085)

### Step 2: Configure Database
```sql
CREATE DATABASE mydb;
CREATE USER 'mkyong'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON mydb.* TO 'mkyong'@'localhost';
FLUSH PRIVILEGES;
```

### Step 3: Run Application
```bash
./mvnw spring-boot:run
```

### Step 4: Access Documentation
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Kafka UI**: `http://localhost:8085`

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
Response: true
```

#### Get Partant Performance Score
```bash
GET /pmu/partant/1/performance
Response: 85
```

## 🔄 Kafka Integration

### Topics
- **`pmu-events`** - Main topic for course events

### Monitoring Kafka Events
To see course events sent to Kafka:
1. Open Kafdrop: `http://localhost:8085`
2. Navigate to Topics → `pmu-events`
3. View messages in real-time

### Event Flow
1. Create/update course via REST API
2. Event published to `pmu-events` topic
3. Kafka consumer processes the event
4. Data persisted to MySQL database

## 🏗️ Project Architecture

### Domain Layer
- **`CourseRecord`** & **`PartantRecord`** - Immutable domain models
- **`CourseDomainService`** - Pure business logic for courses
- **`PartantDomainService`** - Pure business logic for partants

### Service Layer
- **`PmuCourseService`** - Course management orchestration
- **`PmuPartantService`** - Partant management orchestration
- **`CourseEventPublisher`** - Kafka event publishing

### Validation Layer
- **`CourseValidator`** - Course data validation
- **`PartantValidator`** - Partant data validation
- **`ValidationConfig`** - Simplified validation rules with Lombok @Data and @ConfigurationProperties

### Infrastructure Layer
- **JPA Entities & Repositories** - Database persistence
- **REST Controllers** - HTTP API endpoints
- **Kafka Components** - Message streaming

## ✅ Validation System

### Validation Layers
1. **Domain Models** - Bean Validation annotations for format checks
2. **Validation Components** - Existence and integrity validation
3. **Domain Services** - Pure business logic validation
4. **Service Layer** - Validation orchestration

### Key Validation Rules
- **Courses**: 3-20 partants, max 6 months in future, unique sequential numbers
- **Partants**: 2-50 character names, numbers 1-99
- **Betting Eligibility**: 5-15 partants, at least 24 hours in future

## 🧪 Testing

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=PmuCourseServiceTest

# Run only Kafka integration tests
./mvnw test -Dtest=CourseProducerTest,CourseConsumerTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Structure
- **Unit Tests** - Domain services and validation logic (226/226 passing)
- **Kafka Integration Tests** - Producer and consumer tests (4/4 passing)
- **Repository Tests** - Database layer testing
- **Total Status**: 230/230 tests passing (0 failures, 0 errors, 2 skipped)

### Kafka Test Configuration
- **EmbeddedKafka**: Custom port 3333 for test isolation
- **Template Types**: Properly configured `KafkaTemplate<Integer, CourseRecord>` (@Primary)
- **Test Isolation**: @DirtiesContext ensures clean test environment
- **Coverage**: Both producer (`CourseProducerTest`) and consumer (`CourseConsumerTest`) tests

## 🔧 Configuration

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3308/mydb
spring.datasource.username=mkyong
spring.datasource.password=password

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.topic.name=pmu-events
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.IntegerSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Validation Rules
pmu.validation.course.max-future-months=6
pmu.validation.course.min-partants=3
pmu.validation.partant.name-max-length=50
```

## 📖 Reference Documentation

### Spring Framework
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Web MVC](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/index.html#web)
- [Spring Data JPA](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
- [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/index.html#messaging.kafka)

### Additional Technologies
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- [TestContainers](https://www.testcontainers.org/)
- [Flyway Migrations](https://flywaydb.org/documentation/)

## 🔍 Troubleshooting

### Database Issues
```bash
# Check MySQL container
docker ps | grep mysql

# Check container logs
docker logs <mysql-container-id>
```

### Kafka Issues
```bash
# Check Kafka container
docker ps | grep kafka

# Check Kafka topics
docker exec -it <kafka-container-id> kafka-topics.sh --list --bootstrap-server localhost:9092
```

### Application Issues
```bash
# Check application logs
./mvnw spring-boot:run

# Run Flyway migrations
./mvnw flyway:migrate
```

### Health Checks
- **Application Health**: `http://localhost:8080/actuator/health`
- **MySQL Health**: Check Docker container status
- **Kafka Health**: `http://localhost:8085` (Kafdrop)

## 🚀 Advanced Features

### Business Logic Endpoints
- **Course Difficulty Calculation**: `/pmu/course/{id}/difficulty` (1-10 scale)
- **Partant Performance Scoring**: `/pmu/partant/{id}/performance` (1-100 scale)
- **Skill Categorization**: `/pmu/partant/{id}/skill-category` (EXPERT/ADVANCED/INTERMEDIATE/NOVICE)

### Event-Driven Architecture
- Course creation/update events automatically published to Kafka
- Configurable retry mechanisms and dead letter topics
- Real-time event monitoring via Kafdrop

## 📞 Support

For technical support:
1. Check the troubleshooting section above
2. Review application logs for detailed error messages
3. Verify Docker containers are running properly
4. Test API endpoints using Swagger UI
5. Check comprehensive documentation in `GUIDE.md` and `README.md`
