package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.admin.PlatformStatsResponse;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.dto.user.UserResponse;
import com.example.ketari.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling administrator platform governance, employer approvals, and analytics.
 */
@Tag(name = "Administrator", description = "Endpoints for platform governance, employer vetting, and platform metrics")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get list of pending employer registrations awaiting approval")
    @GetMapping("/employers/pending")
    public PageResponse<UserResponse> getPendingEmployers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return this.adminService.getPendingEmployers(page, size);
    }

    @Operation(summary = "Approve or reject a pending employer registration")
    @PatchMapping("/employers/{userId}/approve")
    public ApiResponse<UserResponse> approveEmployer(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "true") boolean approve
    ) {
        UserResponse response = this.adminService.approveEmployer(userId, approve);
        String msg = approve ? "Employer approved successfully" : "Employer approval rejected";
        return ApiResponse.success(msg, response);
    }

    @Operation(summary = "Get platform-wide operational metrics and analytics")
    @GetMapping("/stats")
    public ApiResponse<PlatformStatsResponse> getPlatformStats() {
        PlatformStatsResponse response = this.adminService.getPlatformStats();
        return ApiResponse.success("Platform statistics retrieved successfully", response);
    }

    @Operation(summary = "Get all job listings for admin inspection")
    @GetMapping("/jobs")
    public PageResponse<JobResponse> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return this.adminService.getAllJobsAdmin(page, size);
    }

    @Operation(summary = "Force-delete any inappropriate or violating job posting")
    @DeleteMapping("/jobs/{jobId}")
    public ApiResponse<Void> deleteJob(@PathVariable Long jobId) {
        this.adminService.deleteJobAdmin(jobId);
        return ApiResponse.success("Job posting deleted by administrator", null);
    }
}
