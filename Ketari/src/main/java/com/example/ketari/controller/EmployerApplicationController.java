package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.application.ApplicationResponse;
import com.example.ketari.dto.application.ApplicationStatusUpdateRequest;
import com.example.ketari.model.User;
import com.example.ketari.service.ApplicationService;
import com.example.ketari.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller allowing employers to review received candidate applications and update stage statuses.
 */
@Tag(name = "Employer Applications", description = "Endpoints for employers to review candidates and update application statuses")
@RestController
@RequestMapping("/api/employer/applications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
public class EmployerApplicationController {

    private final ApplicationService applicationService;
    private final AuthService authService;

    @Operation(summary = "List candidate applications received for jobs posted by this employer")
    @GetMapping
    public PageResponse<ApplicationResponse> getReceivedApplications(
            @RequestParam(required = false) Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        return this.applicationService.getEmployerApplications(jobId, currentUser.getId(), page, size);
    }

    @Operation(summary = "Update the hiring stage/status of a candidate application")
    @PatchMapping("/{id}/status")
    public ApiResponse<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request
    ) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        ApplicationResponse response = this.applicationService.updateApplicationStatus(id, request, currentUser.getId());
        return ApiResponse.success("Application status updated successfully", response);
    }
}
