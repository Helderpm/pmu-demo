package com.pmu2.exec.infrastructure.rest;

import com.pmu2.exec.domain.CourseRecord;
import com.pmu2.exec.service.PmuCourseService;
import com.pmu2.exec.service.mapper.CourseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmu/course")
@Tag(name = "Course Management", description = "Course management APIs for PMU system")
public class PmuCourseController {

    private final PmuCourseService pmuCourseService;
    private final CourseMapper courseMapper;

    public PmuCourseController(PmuCourseService pmuCourseService, CourseMapper courseMapper) {
        this.pmuCourseService = pmuCourseService;
        this.courseMapper = courseMapper;
    }

    @Operation(summary = "Get all courses", description = "Retrieve all courses in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all courses")
    })
    @GetMapping
    public List<CourseRecord> findAll() {
        return courseMapper.toRecordList(pmuCourseService.findAll());
    }

    @Operation(summary = "Create a new course", description = "Create a new course with validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Course created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CourseRecord create(@RequestBody CourseRecord course) {
        return courseMapper.toRecord(pmuCourseService.saveEvent(course));
    }

    @Operation(summary = "Delete a course", description = "Delete a course by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@Parameter(description = "Course ID") @PathVariable Long id) {
        pmuCourseService.deleteById(id);
    }

    @Operation(summary = "Find courses by name", description = "Search for courses by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved courses")
    })
    @GetMapping("/find/{name}")
    public List<CourseRecord> findByName(@Parameter(description = "Course name") @PathVariable String name) {
        return courseMapper.toRecordList(pmuCourseService.findByName(name));
    }

    @Operation(summary = "Check betting eligibility", description = "Check if a course is eligible for betting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully checked eligibility"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}/betting-eligible")
    public boolean isEligibleForBetting(@Parameter(description = "Course ID") @PathVariable Long id) {
        return pmuCourseService.isEligibleForBetting(id);
    }

    @Operation(summary = "Get course difficulty", description = "Calculate the difficulty score of a course (1-10)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully calculated difficulty"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}/difficulty")
    public int getCourseDifficulty(@Parameter(description = "Course ID") @PathVariable Long id) {
        return pmuCourseService.calculateCourseDifficulty(id);
    }
}

