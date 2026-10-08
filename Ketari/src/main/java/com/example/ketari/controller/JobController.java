package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.WorkplaceType;
import com.example.ketari.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller providing public job search, filtering, categories, and detail endpoints.
 */
@Tag(name = "Public Jobs", description = "Endpoints for public job search, category discovery, and job viewing")
@RestController
@RequestMapping("/api/jobs/public")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(summary = "Search and filter open job postings with pagination")
    @GetMapping
    public PageResponse<JobResponse> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) WorkplaceType workplaceType,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String skill,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return this.jobService.getPublicJobs(
                keyword,
                location,
                workplaceType,
                employmentType,
                category,
                experienceLevel,
                skill,
                page,
                size,
                sortBy,
                direction
        );
    }

    @Operation(summary = "Get detailed information for a specific job posting")
    @GetMapping("/{id}")
    public ApiResponse<JobResponse> getJobById(@PathVariable Long id) {
        JobResponse response = this.jobService.getJobById(id);
        return ApiResponse.success("Job retrieved successfully", response);
    }

    @Operation(summary = "Get list of available job categories")
    @GetMapping("/categories")
    public ApiResponse<List<String>> getCategories() {
        List<String> categories = this.jobService.getCategories();
        return ApiResponse.success("Categories retrieved successfully", categories);
    }
}
