package com.pmu2.exec.infrastructure.rest;

import com.pmu2.exec.domain.PartantRecord;
import com.pmu2.exec.service.PmuPartantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmu/partant")
@Tag(name = "Partant Management", description = "Partant (horse) management APIs for PMU system")
public class PmuPartantController {

    private final PmuPartantService pmuPartantService;

    public PmuPartantController(PmuPartantService pmuPartantService) {
        this.pmuPartantService = pmuPartantService;
    }

    @Operation(summary = "Get all partants", description = "Retrieve all partants (horses) in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all partants")
    })
    @GetMapping
    public List<PartantRecord> findAll() {
        return pmuPartantService.findAll();
    }

    @Operation(summary = "Create a new partant", description = "Create a new partant (horse) with validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partant created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PartantRecord create(@RequestBody PartantRecord partant) {
        return pmuPartantService.save(partant);
    }

    @Operation(summary = "Delete a partant", description = "Delete a partant by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Partant deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Partant not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@Parameter(description = "Partant ID") @PathVariable Long id) {
        pmuPartantService.deleteById(id);
    }

    @Operation(summary = "Find partants by name", description = "Search for partants by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved partants")
    })
    @GetMapping("/find/{name}")
    public List<PartantRecord> findByName(@Parameter(description = "Partant name") @PathVariable String name) {
        return pmuPartantService.findByName(name);
    }

    @Operation(summary = "Check good standing", description = "Check if a partant is in good standing for racing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully checked standing"),
        @ApiResponse(responseCode = "404", description = "Partant not found")
    })
    @GetMapping("/{id}/good-standing")
    public boolean isInGoodStanding(@Parameter(description = "Partant ID") @PathVariable Long id) {
        return pmuPartantService.isInGoodStanding(id);
    }

    @Operation(summary = "Get performance score", description = "Calculate the performance score of a partant (1-100)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully calculated performance score"),
        @ApiResponse(responseCode = "404", description = "Partant not found")
    })
    @GetMapping("/{id}/performance")
    public int getPerformanceScore(@Parameter(description = "Partant ID") @PathVariable Long id) {
        return pmuPartantService.calculatePerformanceScore(id);
    }

    @Operation(summary = "Get skill category", description = "Determine the skill category of a partant (NOVICE/INTERMEDIATE/ADVANCED/EXPERT)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully determined skill category"),
        @ApiResponse(responseCode = "404", description = "Partant not found")
    })
    @GetMapping("/{id}/skill-category")
    public String getSkillCategory(@Parameter(description = "Partant ID") @PathVariable Long id) {
        return pmuPartantService.determineSkillCategory(id);
    }
}

