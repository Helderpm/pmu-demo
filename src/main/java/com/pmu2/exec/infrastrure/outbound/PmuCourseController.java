package com.pmu2.exec.infrastrure.outbound;

import com.pmu2.exec.infrastrure.inbound.CourseEntity;
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
    public List<CourseEntity> findAll() {
        return pmuCourseService.findAll();
    }

    // create a book
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public CourseEntity create(@RequestBody CourseEntity course) {
        return pmuCourseService.save(course);
    }

    // delete a book
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        pmuCourseService.deleteById(id);
    }

    @GetMapping("/find/{name}")
    public List<CourseEntity> findByName(@PathVariable String name) {
        return pmuCourseService.findByName(name);
    }
}
