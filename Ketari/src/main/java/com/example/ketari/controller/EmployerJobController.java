package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.job.JobRequest;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.model.User;
import com.example.ketari.service.AuthService;
import com.example.ketari.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller allowing employers to post, update, list, and delete their job openings.
 */
@Tag(name = "Employer Jobs", description = "Endpoints for employers to post and manage job listings")
@RestController
@RequestMapping("/api/jobs/employer")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
public class EmployerJobController {

    private final JobService jobService;
    private final AuthService authService;

    @Operation(summary = "Get jobs posted by the currently authenticated employer")
    @GetMapping("/my-jobs")
    public PageResponse<JobResponse> getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        return this.jobService.getEmployerJobs(currentUser.getId(), page, size);
    }

    @Operation(summary = "Create a new job posting")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        JobResponse response = this.jobService.createJob(request, currentUser.getId());
        return ApiResponse.success("Job posting created successfully", response);
    }

    @Operation(summary = "Update an existing job posting")
    @PutMapping("/{id}")
    public ApiResponse<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request
    ) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        JobResponse response = this.jobService.updateJob(id, request, currentUser.getId());
        return ApiResponse.success("Job posting updated successfully", response);
    }

    @Operation(summary = "Delete a job posting")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteJob(@PathVariable Long id) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        this.jobService.deleteJob(id, currentUser.getId());
        return ApiResponse.success("Job posting deleted successfully", null);
    }
}
