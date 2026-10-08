package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.profile.EmployerProfileRequest;
import com.example.ketari.dto.profile.EmployerProfileResponse;
import com.example.ketari.model.User;
import com.example.ketari.service.AuthService;
import com.example.ketari.service.EmployerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller managing employer company profile, details, and branding.
 */
@Tag(name = "Employer Profile", description = "Endpoints for employers to view and update their company profile")
@RestController
@RequestMapping("/api/employer/profile")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;
    private final AuthService authService;

    @Operation(summary = "Get company profile of the authenticated employer")
    @GetMapping
    public ApiResponse<EmployerProfileResponse> getProfile() {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        EmployerProfileResponse response = this.employerProfileService.getProfile(currentUser.getId());
        return ApiResponse.success("Employer profile retrieved successfully", response);
    }

    @Operation(summary = "Update employer company profile details")
    @PutMapping
    public ApiResponse<EmployerProfileResponse> updateProfile(@Valid @RequestBody EmployerProfileRequest request) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        EmployerProfileResponse response = this.employerProfileService.updateProfile(request, currentUser.getId());
        return ApiResponse.success("Employer profile updated successfully", response);
    }

    @Operation(summary = "Upload employer company logo")
    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EmployerProfileResponse> uploadLogo(@RequestParam("file") MultipartFile file) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        EmployerProfileResponse response = this.employerProfileService.uploadCompanyLogo(file, currentUser.getId());
        return ApiResponse.success("Company logo uploaded successfully", response);
    }
}
