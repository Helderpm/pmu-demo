package com.pmu2.exec.service.mapper;

import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.infrastrure.db.sql.PartantEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * This interface provides methods for mapping between {@link PartantEntity} and {@link PartantRecord}.
 * It uses MapStruct for automatic mapping between the two types.
 */
@Mapper(componentModel = "spring")
public interface PartantMapper {
    
    /**
     * Maps a single {@link PartantEntity} to a {@link PartantRecord}.
     *
     * @param partantEntity the entity to map
     * @return the mapped record
     */
    PartantRecord toReccord(PartantEntity partantEntity);

    /**
     * Maps a collection of {@link PartantEntity} to a collection of {@link PartantRecord}.
     *
     * @param partantEntity the entities to map
     * @return the mapped records
     */
    List<PartantRecord> toReccordList(Iterable<PartantEntity> partantEntity);

    /**
     * Maps a single {@link PartantRecord} to a {@link PartantEntity}.
     *
     * @param partantEntity the record to map
     * @return the mapped entity
     */
    PartantEntity toEntity(PartantRecord partantEntity);

    /**
     * Maps a collection of {@link PartantRecord} to a collection of {@link PartantEntity}.
     *
     * @param partantEntitys the records to map
     * @return the mapped entities
     */
    List<PartantEntity> toEntityList(Iterable<PartantRecord> partantEntitys);
}
