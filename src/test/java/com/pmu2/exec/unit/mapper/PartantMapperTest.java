package com.pmu2.exec.unit.mapper;

import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.infrastructure.db.sql.PartantEntity;
import com.pmu2.exec.service.mapper.PartantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartantMapperTest {

    private PartantMapper partantMapper;

    @BeforeEach
    void setUp() {
        partantMapper = Mappers.getMapper(PartantMapper.class);
    }

    @Test
    void shouldMapPartantEntityToPartantRecord() {
        // Given
        PartantEntity partantEntity = new PartantEntity();
        partantEntity.setId(100L);
        partantEntity.setName("Thunder Bolt");
        partantEntity.setNumber(5);

        // When
        PartantRecord partantRecord = partantMapper.toRecord(partantEntity);

        // Then
        assertNotNull(partantRecord);
        assertEquals(Integer.valueOf(100), partantRecord.id());
        assertEquals("Thunder Bolt", partantRecord.name());
        assertEquals(5, partantRecord.number());
    }

    @Test
    void shouldMapNullPartantEntityToNullRecord() {
        // Given
        PartantEntity partantEntity = null;

        // When
        PartantRecord partantRecord = partantMapper.toRecord(partantEntity);

        // Then
        assertNull(partantRecord);
    }

    @Test
    void shouldMapPartantRecordToPartantEntity() {
        // Given
        PartantRecord partantRecord = new PartantRecord(100, "Thunder Bolt", 5);

        // When
        PartantEntity partantEntity = partantMapper.toEntity(partantRecord);

        // Then
        assertNotNull(partantEntity);
        assertEquals(100L, partantEntity.getId().longValue());
        assertEquals("Thunder Bolt", partantEntity.getName());
        assertEquals(5, partantEntity.getNumber());
    }

    @Test
    void shouldMapNullPartantRecordToNullEntity() {
        // Given
        PartantRecord partantRecord = null;

        // When
        PartantEntity partantEntity = partantMapper.toEntity(partantRecord);

        // Then
        assertNull(partantEntity);
    }

    @Test
    void shouldMapPartantEntityListToPartantRecordList() {
        // Given
        PartantEntity partant1 = new PartantEntity();
        partant1.setId(1L);
        partant1.setName("Horse 1");
        partant1.setNumber(1);

        PartantEntity partant2 = new PartantEntity();
        partant2.setId(2L);
        partant2.setName("Horse 2");
        partant2.setNumber(2);

        List<PartantEntity> partantEntities = List.of(partant1, partant2);

        // When
        List<PartantRecord> partantRecords = partantMapper.toRecordList(partantEntities);

        // Then
        assertNotNull(partantRecords);
        assertEquals(2, partantRecords.size());
        
        PartantRecord record1 = partantRecords.get(0);
        assertEquals(Integer.valueOf(1), record1.id());
        assertEquals("Horse 1", record1.name());
        assertEquals(1, record1.number());

        PartantRecord record2 = partantRecords.get(1);
        assertEquals(Integer.valueOf(2), record2.id());
        assertEquals("Horse 2", record2.name());
        assertEquals(2, record2.number());
    }

    @Test
    void shouldMapEmptyPartantEntityListToEmptyRecordList() {
        // Given
        List<PartantEntity> partantEntities = List.of();

        // When
        List<PartantRecord> partantRecords = partantMapper.toRecordList(partantEntities);

        // Then
        assertNotNull(partantRecords);
        assertEquals(0, partantRecords.size());
    }

    @Test
    void shouldMapNullPartantEntityListToNullRecordList() {
        // Given
        List<PartantEntity> partantEntities = null;

        // When
        List<PartantRecord> partantRecords = partantMapper.toRecordList(partantEntities);

        // Then
        assertNull(partantRecords);
    }

    @Test
    void shouldMapPartantRecordListToPartantEntityList() {
        // Given
        PartantRecord record1 = new PartantRecord(1, "Horse 1", 1);
        PartantRecord record2 = new PartantRecord(2, "Horse 2", 2);
        List<PartantRecord> partantRecords = List.of(record1, record2);

        // When
        List<PartantEntity> partantEntities = partantMapper.toEntityList(partantRecords);

        // Then
        assertNotNull(partantEntities);
        assertEquals(2, partantEntities.size());

        PartantEntity entity1 = partantEntities.get(0);
        assertEquals(1L, entity1.getId());
        assertEquals("Horse 1", entity1.getName());
        assertEquals(1, entity1.getNumber());

        PartantEntity entity2 = partantEntities.get(1);
        assertEquals(2L, entity2.getId());
        assertEquals("Horse 2", entity2.getName());
        assertEquals(2, entity2.getNumber());
    }

    @Test
    void shouldMapEmptyPartantRecordListToEmptyEntityList() {
        // Given
        List<PartantRecord> partantRecords = List.of();

        // When
        List<PartantEntity> partantEntities = partantMapper.toEntityList(partantRecords);

        // Then
        assertNotNull(partantEntities);
        assertEquals(0, partantEntities.size());
    }

    @Test
    void shouldMapNullPartantRecordListToNullEntityList() {
        // Given
        List<PartantRecord> partantRecords = null;

        // When
        List<PartantEntity> partantEntities = partantMapper.toEntityList(partantRecords);

        // Then
        assertNull(partantEntities);
    }

    @Test
    void shouldMapPartantEntityWithNullValues() {
        // Given
        PartantEntity partantEntity = new PartantEntity();
        partantEntity.setId(null);
        partantEntity.setName(null);
        partantEntity.setNumber(0);

        // When
        PartantRecord partantRecord = partantMapper.toRecord(partantEntity);

        // Then
        assertNotNull(partantRecord);
        assertNull(partantRecord.id());
        assertNull(partantRecord.name());
        assertEquals(0, partantRecord.number());
    }

    @Test
    void shouldMapPartantRecordWithNullValues() {
        // Given
        PartantRecord partantRecord = new PartantRecord(null, null, 0);

        // When
        PartantEntity partantEntity = partantMapper.toEntity(partantRecord);

        // Then
        assertNotNull(partantEntity);
        assertNull(partantEntity.getId());
        assertNull(partantEntity.getName());
        assertEquals(0, partantEntity.getNumber());
    }
}
