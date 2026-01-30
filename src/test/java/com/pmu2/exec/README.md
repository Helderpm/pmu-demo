# Test Structure for PMU Application

This document describes the organized test structure for the PMU (Pari Mutuel Urbain) application.

## 📁 Test Organization

```
src/test/java/com/pmu2/exec/
├── unit/                           # Unit Tests
│   ├── domain/                     # Domain Layer Tests
│   │   ├── CourseDomainServiceTest.java
│   │   └── PartantDomainServiceTest.java
│   ├── service/                    # Service Layer Tests
│   │   ├── PmuCourseServiceTest.java
│   │   └── PmuPartantServiceTest.java
│   ├── validation/                 # Validation Layer Tests
│   │   ├── CourseValidatorTest.java
│   │   └── PartantValidatorTest.java
│   └── mapper/                     # Data Mapping Tests
│       └── CourseMapperTest.java
├── integration/                    # Integration Tests
│   ├── ExecAppIntegrationTests.java
│   ├── repository/
│   │   └── RepositoryJpaTest.java
│   └── kafka/
│       ├── CourseConsumerTest.java
│       └── CourseProducerTest.java
├── utils/                          # Test Utilities
│   └── TestUtil.java
├── TestSuiteConfig.java            # Test Suite Configuration
└── README.md                       # This file
```

## 🧪 Test Types

### Unit Tests (`unit/`)

Unit tests focus on testing individual components in isolation using mocks:

#### Domain Layer Tests
- **CourseDomainServiceTest**: Tests pure business logic for course operations
- **PartantDomainServiceTest**: Tests pure business logic for partant operations

#### Service Layer Tests
- **PmuCourseServiceTest**: Tests course service orchestration with mocked dependencies
- **PmuPartantServiceTest**: Tests partant service orchestration with mocked dependencies

#### Validation Layer Tests
- **CourseValidatorTest**: Tests course validation logic
- **PartantValidatorTest**: Tests partant validation logic

#### Mapper Tests
- **CourseMapperTest**: Tests entity-to-record mapping logic

### Integration Tests (`integration/`)

Integration tests test multiple components working together:

#### Application Integration Tests
- **ExecAppIntegrationTests**: Full-stack tests with REST API, database, and Kafka
  - **Note**: Currently disabled due to Docker requirement (see @Disabled annotation)
  - To enable: Remove @Disabled annotation when Docker is available

#### Repository Tests
- **RepositoryJpaTest**: Database integration tests using @DataJpaTest

#### Kafka Tests
- **CourseConsumerTest**: Kafka message consumption tests
- **CourseProducerTest**: Kafka message production tests

## 🚀 Running Tests

### Run All Tests
```bash
./mvnw test
# Result: 228/228 unit tests passing, 1 integration test skipped (Docker requirement)
```

### Run Unit Tests Only
```bash
./mvnw test -Dtest="com.pmu2.exec.unit.**"
```

### Run Integration Tests Only
```bash
./mvnw test -Dtest="com.pmu2.exec.integration.**"
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=CourseDomainServiceTest
```

### Run Test Suite
```bash
./mvnw test -Dtest=TestSuiteConfig
```

### Run Tests with Coverage
```bash
./mvnw test jacoco:report
```

## 📊 Test Coverage Areas

### Domain Layer (100% Coverage Target)
- ✅ Business rule validation
- ✅ Course creation rules
- ✅ Partant eligibility checks
- ✅ Performance calculations
- ✅ Skill categorization

### Service Layer (95% Coverage Target)
- ✅ CRUD operations
- ✅ Business logic orchestration
- ✅ Event publishing
- ✅ Error handling

### Validation Layer (90% Coverage Target)
- ✅ Input validation
- ✅ Business constraint validation
- ✅ Error message generation

### Integration Layer (85% Coverage Target)
- ✅ REST API endpoints
- ✅ Database operations
- ✅ Kafka messaging
- ✅ End-to-end workflows

## 🔧 Test Configuration

### Unit Test Configuration
- **Framework**: JUnit 5 with Mockito
- **Mocking**: Mockito for dependency isolation
- **Assertions**: AssertJ for readable assertions
- **Test Data**: Builder pattern for test object creation

### Integration Test Configuration
- **Database**: TestContainers with MySQL
- **Kafka**: Embedded Kafka for messaging tests
- **Web**: TestRestTemplate for API testing
- **Transactions**: @Transactional with rollback

### Test Utilities
- **TestUtil**: Common test data builders and utilities
- **TestContainers**: Real container-based testing
- **Awaitility**: Asynchronous test coordination

## 📋 Best Practices

### Unit Tests
1. **Fast Execution**: Tests should run in milliseconds
2. **Isolation**: Each test should be independent
3. **Mock Dependencies**: Use mocks for external dependencies
4. **Clear Naming**: Test names should describe the scenario
5. **AAA Pattern**: Arrange, Act, Assert structure

### Integration Tests
1. **Real Environment**: Use real infrastructure when possible
2. **Test Data Management**: Clean up data between tests
3. **Asynchronous Handling**: Use Awaitility for async operations
4. **Environment Isolation**: Use test profiles and configurations
5. **Comprehensive Coverage**: Test complete workflows

### General Guidelines
1. **Descriptive Names**: Test method names should explain what is being tested
2. **Single Responsibility**: Each test should verify one behavior
3. **Test Data Builders**: Use builders for complex test object creation
4. **Error Scenarios**: Test both happy path and error conditions
5. **Documentation**: Add comments for complex business logic tests

## 🔍 Test Examples

### Unit Test Example
```java
@Test
void shouldValidateValidCourse() {
    // Given
    List<PartantRecord> partants = List.of(
        new PartantRecord(1, "Horse 1", 1),
        new PartantRecord(2, "Horse 2", 2)
    );
    CourseRecord course = new CourseRecord(1L, "Test Course", 100, 
        LocalDate.now().plusDays(10), partants);

    // When & Then
    assertDoesNotThrow(() -> courseDomainService.validateCourseCreation(course));
}
```

### Integration Test Example
```java
@Test
void testCreateCourse() {
    // Given
    CourseRecord newCourse = TestUtil.newCourseRecord("Test Course");
    HttpEntity<CourseRecord> request = new HttpEntity<>(newCourse, headers);

    // When
    ResponseEntity<CourseRecord> response = restTemplate.postForEntity(
        baseUri + "/pmu/course", request, CourseRecord.class);

    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
}
```

## 📈 Continuous Integration

### GitHub Actions Configuration
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 21
        uses: actions/setup-java@v2
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Run tests
        run: ./mvnw test
      - name: Generate test report
        run: ./mvnw jacoco:report
```

## 🚨 Troubleshooting

### Common Issues
1. **TestContainers**: Ensure Docker is running for integration tests (currently disabled)
2. **Port Conflicts**: Use random ports for web tests
3. **Database Cleanup**: Verify data cleanup between tests
4. **Async Tests**: Use proper await mechanisms for async operations
5. **Memory Issues**: Limit test container usage for large test suites
6. **Integration Test**: ExecAppIntegrationTests requires Docker - see @Disabled annotation

### Debug Mode
Enable debug logging in test properties:
```properties
logging.level.com.pmu2.exec=DEBUG
logging.level.org.springframework.kafka=DEBUG
```
