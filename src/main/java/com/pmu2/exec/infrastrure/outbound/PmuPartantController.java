package com.pmu2.exec.infrastrure.outbound;

import com.pmu2.exec.infrastrure.inbound.CourseEntity;
import com.pmu2.exec.infrastrure.inbound.PartantEntity;
import com.pmu2.exec.service.PmuPartantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmu/partant")
@Tag(name = "Tutorial", description = "Tutorial management APIs")
public class PmuPartantController {

    @Autowired
    private PmuPartantService pmuPartantService;

    //find all courses in the system
    @GetMapping
    public List<PartantEntity> findAll() {
        return pmuPartantService.findAll();
    }

    // create a partant
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public PartantEntity create(@RequestBody PartantEntity partant) {
        return pmuPartantService.save(partant);
    }

    // delete a book
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        pmuPartantService.deleteById(id);
    }

    @GetMapping("/find/{name}")
    public List<PartantEntity> findByName(@PathVariable String name) {
        return pmuPartantService.findByName(name);
    }
}
