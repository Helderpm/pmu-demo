package com.pmu2.exec.service;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.infrastructure.kafka.producer.PmuProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service responsible for publishing course-related events to Kafka.
 * This service separates event publishing concerns from business logic.
 */
@Service
@RequiredArgsConstructor
public class CourseEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CourseEventPublisher.class);

    private final PmuProducerService producerService;

    /**
     * Publishes a course creation event to Kafka.
     *
     * @param course the course record to publish
     * @return CompletableFuture representing the publish operation
     */
    public CompletableFuture<Void> publishCourseCreated(CourseRecord course) {
        log.info("Publishing course created event for course ID: {}", course.courseId());
        return producerService.sendMessageToKafka(course)
                .thenRun(() -> log.info("Successfully published course created event for course ID: {}", course.courseId()))
                .exceptionally(throwable -> {
                    log.error("Failed to publish course created event for course ID: {}", course.courseId(), throwable);
                    return null;
                });
    }

    /**
     * Publishes a course update event to Kafka.
     *
     * @param course the course record to publish
     * @return CompletableFuture representing the publish operation
     */
    public CompletableFuture<Void> publishCourseUpdated(CourseRecord course) {
        log.info("Publishing course updated event for course ID: {}", course.courseId());
        return producerService.sendMessageToKafka(course)
                .thenRun(() -> log.info("Successfully published course updated event for course ID: {}", course.courseId()))
                .exceptionally(throwable -> {
                    log.error("Failed to publish course updated event for course ID: {}", course.courseId(), throwable);
                    return null;
                });
    }

    /**
     * Publishes a course deletion event to Kafka.
     *
     * @param courseId the ID of the deleted course
     * @return CompletableFuture representing the publish operation
     */
    public CompletableFuture<Void> publishCourseDeleted(Long courseId) {
        log.info("Publishing course deleted event for course ID: {}", courseId);
        return CompletableFuture.completedFuture(null)
                .thenRun(() -> log.info("Successfully published course deleted event for course ID: {}", courseId))
                .exceptionally(throwable -> {
                    log.error("Failed to publish course deleted event for course ID: {}", courseId, throwable);
                    return null;
                });
    }
}

