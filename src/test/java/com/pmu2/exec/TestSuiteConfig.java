package com.pmu2.exec;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test Suite for PMU Application
 * 
 * This suite includes all test classes to provide a comprehensive view of test coverage.
 * Run this suite to execute all tests in the application.
 */
@Suite
@SelectClasses({
    // Unit Tests
    com.pmu2.exec.unit.domain.CourseDomainServiceTest.class,
    com.pmu2.exec.unit.domain.PartantDomainServiceTest.class,
    com.pmu2.exec.unit.service.PmuCourseServiceTest.class,
    com.pmu2.exec.unit.service.PmuPartantServiceTest.class,
    com.pmu2.exec.unit.validation.CourseValidatorTest.class,
    com.pmu2.exec.unit.validation.PartantValidatorTest.class,
    com.pmu2.exec.unit.mapper.CourseMapperTest.class,
    
    // Integration Tests
    com.pmu2.exec.integration.ExecAppIntegrationTests.class,
    com.pmu2.exec.integration.repository.RepositoryJpaTest.class,
    com.pmu2.exec.integration.kafka.CourseConsumerTest.class,
    com.pmu2.exec.integration.kafka.CourseProducerTest.class
})
class TestSuiteConfig {
}
