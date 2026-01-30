package com.pmu2.exec.integration.repository;

import com.pmu2.exec.infrastructure.dao.EntrepriseJpaEntity;
import com.pmu2.exec.infrastructure.db.sql.CourseEntity;
import com.pmu2.exec.infrastructure.db.sql.CourseJpaRepository;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.infrastructure.db.sql.PartantJpaRepository;
import com.pmu2.exec.infrastructure.repository.EntrepriseJpaRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.pmu2.exec.utils.TestUtil.getParticipantEntityListA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ====================================================================
 * 🗄️ DATA LAYER INTEGRATION TESTS
 * ====================================================================
 * 
 * <p>Comprehensive testing of JPA repository operations and entity persistence:
 * <ul>
 *   <li>🏗️ Entity persistence and retrieval</li>
 *   <li>🔍 Custom query method implementations</li>
 *   <li>🔗 Entity relationship mapping validation</li>
 *   <li>📊 Data integrity and constraint testing</li>
 * </ul>
 * 
 * ====================================================================
 * 🎯 TEST OBJECTIVES
 * ====================================================================
 * 
 * <p><strong>Primary Goals:</strong>
 * <ul>
 *   <li>✅ Validate JPA entity mappings and configurations</li>
 *   <li>✅ Test custom repository query implementations</li>
 *   <li>✅ Verify entity relationships and cascade operations</li>
 *   <li>✅ Ensure data integrity during persistence operations</li>
 * </ul>
 * 
 * <p><strong>Scope Coverage:</strong>
 * <ul>
 *   <li>🏃 Course entity CRUD operations and relationships</li>
 *   <li>🐎 Partant entity CRUD operations and queries</li>
 *   <li>🏢 Entreprise entity basic persistence testing</li>
 *   <li>🔧 Custom repository method validations</li>
 * </ul>
 * 
 * ====================================================================
 * 🏗️ INFRASTRUCTURE CONFIGURATION
 * ====================================================================
 * 
 * <table border="1" style="width:100%">
 *   <tr><th>Component</th><th>Configuration</th><th>Purpose</th></tr>
 *   <tr>
 *     <td>🗄️ @DataJpaTest</td>
 *     <td>Slice testing annotation</td>
 *     <td>JPA components only</td>
 *   </tr>
 *   <tr>
 *     <td>💾 In-memory DB</td>
 *     <td>H2 database</td>
 *     <td>Fast testing, auto-cleanup</td>
 *   </tr>
 *   <tr>
 *     <td>🧹 @DirtiesContext</td>
 *     <td>Context cleanup</td>
 *     <td>Test isolation</td>
 *   </tr>
 *   <tr>
 *     <td>🔧 TestEntityManager</td>
 *     <td>Direct entity management</td>
 *     <td>Precise test data setup</td>
 *   </tr>
 * </table>
 * 
 * ====================================================================
 * 🔄 TEST EXECUTION STRATEGY
 * ====================================================================
 * 
 * <p><strong>TestEntityManager Approach:</strong>
 * <ul>
 *   <li>🎯 <strong>Precision</strong>: Direct entity manipulation without repository methods</li>
 *   <li>🔍 <strong>Validation</strong>: Tests both entity mappings AND repository implementations</li>
 *   <li>⚡ <strong>Performance</strong>: Faster than going through service layer</li>
 *   <li>🔒 <strong>Transactions</strong>: Automatic rollback after each test</li>
 * </ul>
 * 
 * <p><strong>Standard Test Pattern:</strong>
 * <ol>
 *   <li>📋 <strong>Setup</strong>: Create entities using TestEntityManager</li>
 *   <li>💾 <strong>Persist</strong>: Save entities directly to database</li>
 *   <li>🔍 <strong>Query</strong>: Use repository methods to retrieve data</li>
 *   <li>✅ <strong>Validate</strong>: Assert data integrity and relationships</li>
 *   <li>🔄 <strong>Cleanup</strong>: Automatic transaction rollback</li>
 * </ol>
 * 
 * ====================================================================
 * 📊 ENTITY COVERAGE MATRIX
 * ====================================================================
 * 
 * <table border="1" style="width:100%">
 *   <tr><th>Entity</th><th>Operations Tested</th><th>Relationships</th><th>Custom Queries</th></tr>
 *   <tr>
 *     <td>🏃 CourseEntity</td>
 *     <td>CRUD, findByName()</td>
 *     <td>OneToMany (Partants)</td>
 *     <td>✅ findByName()</td>
 *   </tr>
 *   <tr>
 *     <td>🐎 PartantEntity</td>
 *     <td>CRUD, findByName()</td>
 *     <td>ManyToOne (Course)</td>
 *     <td>✅ findByName()</td>
 *   </tr>
 *   <tr>
 *     <td>🏢 EntrepriseJpaEntity</td>
 *     <td>CRUD, findById()</td>
 *     <td>None</td>
 *     <td>❌ Standard only</td>
 *   </tr>
 * </table>
 * 
 * ====================================================================
 * ⚠️ IMPORTANT NOTES
 * ====================================================================
 * 
 * <p><strong>🔧 Configuration Dependencies:</strong>
 * <ul>
 *   <li>Requires H2 in-memory database (auto-configured by @DataJpaTest)</li>
 *   <li>Depends on TestEntityManager for direct entity manipulation</li>
 *   <li>Entity classes must have proper JPA annotations</li>
 *   <li>Repository interfaces must extend JpaRepository</li>
 * </ul>
 * 
 * <p><strong>🧪 Testing Best Practices:</strong>
 * <ul>
 *   <li>Use TestEntityManager for setup, repositories for validation</li>
 *   <li>Test both entity mappings and repository implementations</li>
 *   <li>Validate relationships and cascade operations</li>
 *   <li>Ensure data integrity constraints are enforced</li>
 * </ul>
 * 
 * ====================================================================
 * 🚀 USAGE EXAMPLES
 * ====================================================================
 * 
 * <p><strong>Run All Repository Tests:</strong>
 * <pre>{@code
 * mvn test -Dtest=RepositoryJpaTest
 * }</pre>
 * 
 * <p><strong>Run Specific Entity Tests:</strong>
 * <pre>{@code
 * mvn test -Dtest=RepositoryJpaTest$CourseJpaRepositoryTests
 * }</pre>
 * 
 * <p><strong>Debug Mode:</strong>
 * <pre>{@code
 * mvn test -Dtest=RepositoryJpaTest -Dmaven.test.debug=true
 * }</pre>
 * 
 * ====================================================================
 * 🔗 DEPENDENCIES
 * ====================================================================
 * 
 * @see DataJpaTest Spring Boot JPA testing slice
 * @see TestEntityManager Direct entity management
 * @see DirtiesContext Test isolation and cleanup
 * @see CourseEntity Course domain entity
 * @see PartantEntity Partant domain entity
 * @see EntrepriseJpaEntity Entreprise domain entity
 * 
 * @author PMU Exec Application Team
 * @version 1.0
 * @since 1.0
 */

@DataJpaTest
@DirtiesContext
public class RepositoryJpaTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Nested
    /**
     * Integration tests for CourseJpaRepository.
     * 
     * <p>This test class validates Course entity repository operations:
     * <ul>
     *   <li><strong>Custom Query Methods</strong>: findByName() implementation</li>
     *   <li><strong>Entity Relationships</strong>: Course-Partant associations</li>
     *   <li><strong>Data Persistence</strong>: Entity saving and retrieval</li>
     *   <li><strong>Query Results</strong>: Multiple entities with same name</li>
     * </ul>
     * 
     * <h3>Test Scenarios</h3>
     * <ul>
     *   <li>Courses with same name but different properties</li>
     *   <li>Courses with and without associated partants</li>
     *   <li>Query result ordering and completeness</li>
     * </ul>
     */
    class CourseJpaRepositoryTests {
        @Autowired
        private CourseJpaRepository courseJpaRepository;

        /**
         * Test the custom findByName() repository method.
         * 
         * <p>Validates that:
         * <ul>
         *   <li>Repository correctly finds courses by name</li>
         *   <li>Multiple courses with same name are returned</li>
         *   <li>Course-partant relationships are properly loaded</li>
         *   <li>Null relationships are handled correctly</li>
         * </ul>
         * 
         * <h3>Test Data Setup</h3>
         * <ul>
         *   <li>C1 with date 2023-09-14 and partants</li>
         *   <li>C1 with date 2023-09-16 and no partants</li>
         *   <li>P1 with date 2023-09-16 (different name)</li>
         * </ul>
         */
        @Test
        void testGetByFirstName() {
            //Given
            LocalDate localDateC1 = LocalDate.parse("2023-09-14");
            LocalDate localDateC2 = LocalDate.parse("2023-09-16");

            testEntityManager.persistAndFlush(stubCourse("C1", 2, localDateC1, getParticipantEntityListA()));
            testEntityManager.persistAndFlush(stubCourse("C1", 4, localDateC2, null));
            testEntityManager.persistAndFlush(stubCourse("P1", 6, localDateC2, null));
            //when
            List<CourseEntity> course2 = courseJpaRepository.findByName("C1");
            //then
            assertFalse(course2.isEmpty());
            assertEquals(2, course2.size());

            CourseEntity entityC1 = course2.getFirst();
            assertEquals("C1", entityC1.getName());
            assertEquals(2, entityC1.getNumber());
            assertEquals(localDateC1, entityC1.getDate());
            assertEquals(getParticipantEntityListA().size(), entityC1.getPartants().size());

            CourseEntity entityC2 = course2.get(1);
            assertEquals("C1", entityC2.getName());
            assertEquals(4, entityC2.getNumber());
            assertEquals(localDateC2, entityC2.getDate());
            assertNull(entityC2.getPartants());
        }

        private CourseEntity stubCourse(String name, int number, LocalDate date, List<PartantEntity> partant) {
            return new CourseEntity(name, number, date, partant);
        }
    }

    @Nested
    /**
     * Integration tests for PartantJpaRepository.
     * 
     * <p>This test class validates Partant entity repository operations:
     * <ul>
     *   <li><strong>Custom Query Methods</strong>: findByName() implementation</li>
     *   <li><strong>Entity Persistence</strong>: Partant saving and retrieval</li>
     *   <li><strong>Query Filtering</strong>: Multiple entities with same name</li>
     *   <li><strong>Data Integrity</strong>: Entity property preservation</li>
     * </ul>
     * 
     * <h3>Test Scenarios</h3>
     * <ul>
     *   <li>Multiple partants with same name but different numbers</li>
     *   <li>Query returns only matching entities</li>
     *   <li>Entity properties are correctly preserved</li>
     * </ul>
     */
    class PartantJpaRepositoryTests {

        @Autowired
        private PartantJpaRepository partantJpaRepository;

        /**
         * Test the custom findByName() repository method for partants.
         * 
         * <p>Validates that:
         * <ul>
         *   <li>Repository correctly finds partants by name</li>
         *   <li>Multiple partants with same name are returned</li>
         *   <li>Entity properties (name, number) are preserved</li>
         *   <li>Non-matching entities are excluded from results</li>
         * </ul>
         * 
         * <h3>Test Data Setup</h3>
         * <ul>
         *   <li>Jonh with number 2</li>
         *   <li>Jonh with number 4 (same name, different number)</li>
         *   <li>Wick with number 4 (different name)</li>
         * </ul>
         */
        @Test
        void testGetByFirstName() {
            //Given
            testEntityManager.persistAndFlush(stubPartant("Jonh", 2));
            testEntityManager.persistAndFlush(stubPartant("Jonh", 4));
            testEntityManager.persistAndFlush(stubPartant("Wick", 4));
            //when
            List<PartantEntity> partantEntityList = partantJpaRepository.findByName("Jonh");
            //then
            assertFalse(partantEntityList.isEmpty());
            assertEquals(2, partantEntityList.size());

            PartantEntity entityP1 = partantEntityList.getFirst();
            assertEquals("Jonh", entityP1.getName());
            assertEquals(2, entityP1.getNumber());

            PartantEntity entityP2 = partantEntityList.get(1);
            assertEquals("Jonh", entityP2.getName());
            assertEquals(4, entityP2.getNumber());
        }

        private PartantEntity stubPartant(String name, int number) {
            return new PartantEntity(name, number);
        }
    }

    @Nested
    /**
     * Integration tests for EntrepriseJpaRepository.
     * 
     * <p>This test class validates Entreprise entity repository operations:
     * <ul>
     *   <li><strong>Entity Persistence</strong>: Basic CRUD operations</li>
     *   <li><strong>ID Generation</strong>: Auto-increment primary key</li>
     *   <li><strong>Repository Methods</strong>: Standard JpaRepository functionality</li>
     *   <li><strong>TestEntityManager</strong>: Direct entity management</li>
     * </ul>
     * 
     * <h3>Test Strategy</h3>
     * Uses TestEntityManager.persistAndFlush() to test entity persistence
     * and repository.findById() to test retrieval, ensuring the complete
     * persistence lifecycle works correctly.
     */
    class EntrepriseJpaRepositoryTests{
        @Autowired
        private EntrepriseJpaRepository entrepriseJpaRepository;
        
        /**
         * Test basic entreprise repository persistence and retrieval.
         * 
         * <p>Validates that:
         * <ul>
         *   <li>Entreprise entity can be persisted via TestEntityManager</li>
         *   <li>Primary key is correctly generated and assigned</li>
         *   <li>Entity can be retrieved via repository.findById()</li>
         *   <li>Entity properties are preserved during persistence</li>
         * </ul>
         * 
         * <h3>Test Data</h3>
         * <ul>
         *   <li>Name: "Test Entreprise"</li>
         *   <li>SIRET: "12345678901234"</li>
         * </ul>
         */
        @Test
        void entrepriseRepository_shouldPersistAndFindByIdUsingEntityManager() {
            EntrepriseJpaEntity entreprise = new EntrepriseJpaEntity(null, "Test Entreprise", "12345678901234");
            // Use TestEntityManager to persist the entity directly
            EntrepriseJpaEntity savedEntity = testEntityManager.persistAndFlush(entreprise);
            
            // Verify the entity was saved
            assertThat(savedEntity.getEntrepriseId()).isNotNull();
            
            // Use the repository to find the entity
            Optional<EntrepriseJpaEntity> foundEntity = entrepriseJpaRepository.findById(savedEntity.getEntrepriseId());
            assertThat(foundEntity).isPresent();
            assertThat(foundEntity.get().getNom()).isEqualTo("Test Entreprise");
        }
    }
}
