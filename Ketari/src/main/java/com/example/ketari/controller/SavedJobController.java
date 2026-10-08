package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.job.SavedJobResponse;
import com.example.ketari.model.User;
import com.example.ketari.service.AuthService;
import com.example.ketari.service.SavedJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller allowing candidates to save, retrieve, and remove bookmarked jobs.
 */
@Tag(name = "Saved Jobs", description = "Endpoints for bookmarking and managing saved job listings")
@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class SavedJobController {

    private final SavedJobService savedJobService;
    private final AuthService authService;

    @Operation(summary = "Bookmark a job posting")
    @PostMapping("/{jobId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SavedJobResponse> saveJob(@PathVariable Long jobId) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        SavedJobResponse response = this.savedJobService.saveJob(jobId, currentUser.getId());
        return ApiResponse.success("Job bookmarked successfully", response);
    }

    @Operation(summary = "Remove a bookmarked job")
    @DeleteMapping("/{jobId}")
    public ApiResponse<Void> removeSavedJob(@PathVariable Long jobId) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        this.savedJobService.removeSavedJob(jobId, currentUser.getId());
        return ApiResponse.success("Job removed from saved list", null);
    }

    @Operation(summary = "Get list of all bookmarked jobs with pagination")
    @GetMapping
    public PageResponse<SavedJobResponse> getSavedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        return this.savedJobService.getSavedJobs(currentUser.getId(), page, size);
    }

    @Operation(summary = "Check if a specific job is bookmarked by the candidate")
    @GetMapping("/check/{jobId}")
    public ApiResponse<Boolean> isJobSaved(@PathVariable Long jobId) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        boolean saved = this.savedJobService.isJobSaved(jobId, currentUser.getId());
        return ApiResponse.success("Bookmark status retrieved", saved);
    }
}
