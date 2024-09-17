package com.pmu2.exec.infrastrure.rest;

 import com.pmu2.exec.domain.CourseRecord;
 import com.pmu2.exec.service.PmuCourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmu/course")
@Tag(name = "Tutorial", description = "Tutorial management APIs")
public class PmuCourseController {

    @Autowired
    private PmuCourseService pmuCourseService;

    //find all courses in the system
    @GetMapping
    public List<CourseRecord> findAll() {
        return pmuCourseService.findAll();
    }

    // create a book
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public CourseRecord create(@RequestBody CourseRecord course) {
        return pmuCourseService.saveEvent(course);
    }

    // delete a book
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        pmuCourseService.deleteById(id);
    }

    @GetMapping("/find/{name}")
    public List<CourseRecord> findByName(@PathVariable String name) {
        return pmuCourseService.findByName(name);
    }
}
