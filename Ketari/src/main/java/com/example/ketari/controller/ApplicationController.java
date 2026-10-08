package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.application.ApplicationRequest;
import com.example.ketari.dto.application.ApplicationResponse;
import com.example.ketari.dto.application.ApplicationStatusHistoryResponse;
import com.example.ketari.model.User;
import com.example.ketari.service.ApplicationService;
import com.example.ketari.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller handling candidate job applications and status checks.
 */
@Tag(name = "Applications", description = "Endpoints for submitting and viewing candidate job applications")
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final AuthService authService;

    @Operation(summary = "Submit an application for an open job posting")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<ApplicationResponse> apply(@Valid @RequestBody ApplicationRequest request) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        ApplicationResponse response = this.applicationService.apply(request, currentUser.getId());
        return ApiResponse.success("Application submitted successfully", response);
    }

    @Operation(summary = "Get applications submitted by the current candidate")
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public PageResponse<ApplicationResponse> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        return this.applicationService.getApplicantApplications(currentUser.getId(), page, size);
    }

    @Operation(summary = "Get detailed information for a specific application")
    @GetMapping("/{id}")
    public ApiResponse<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        ApplicationResponse response = this.applicationService.getApplicationById(id, currentUser.getId());
        return ApiResponse.success("Application retrieved successfully", response);
    }

    @Operation(summary = "Get status change history and audit trail for an application")
    @GetMapping("/{id}/history")
    public ApiResponse<List<ApplicationStatusHistoryResponse>> getApplicationHistory(@PathVariable Long id) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        List<ApplicationStatusHistoryResponse> history = this.applicationService.getApplicationHistory(id, currentUser.getId());
        return ApiResponse.success("Application history retrieved successfully", history);
    }
}
