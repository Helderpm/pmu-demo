package com.pmu2.exec.service.mapper;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastrure.db.sql.CourseEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * This interface provides methods for mapping between {@link CourseEntity} and {@link CourseRecord}.
 * It uses MapStruct for automatic mapping between the two types.
 *
 */
@Mapper(componentModel = "spring")
public interface CourseMapper {

    /**
     * Maps a single {@link CourseEntity} to a {@link CourseRecord}.
     *
     * @param courseEntity the entity to map
     * @return the mapped record
     */
    CourseRecord toRecord(CourseEntity courseEntity);

    /**
     * Maps a collection of {@link CourseEntity} to a list of {@link CourseRecord}.
     *
     * @param courseEntity the entities to map
     * @return the mapped records
     */
    List<CourseRecord> toRecordList(Iterable<CourseEntity> courseEntity);

    /**
     * Maps a single {@link CourseRecord} to a {@link CourseEntity}.
     *
     * @param courseRecord the record to map
     * @return the mapped entity
     */
    CourseEntity toEntity(CourseRecord courseRecord);

    /**
     * Maps a collection of {@link CourseRecord} to a list of {@link CourseEntity}.
     *
     * @param courseRecords the records to map
     * @return the mapped entities
     */
    List<CourseEntity> toEntityList(Iterable<CourseRecord> courseRecords);
}
