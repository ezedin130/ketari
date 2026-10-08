package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.profile.JobSeekerProfileRequest;
import com.example.ketari.dto.profile.JobSeekerProfileResponse;
import com.example.ketari.model.User;
import com.example.ketari.service.AuthService;
import com.example.ketari.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Controller managing candidate personal and professional profile details.
 */
@Tag(name = "Job Seeker Profile", description = "Endpoints for candidates to view and manage their career profile")
@RestController
@RequestMapping("/api/job-seeker/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class JobSeekerProfileController {

    private final ProfileService profileService;
    private final AuthService authService;

    @Operation(summary = "Get the candidate profile of the authenticated user")
    @GetMapping
    public ApiResponse<JobSeekerProfileResponse> getProfile() {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        JobSeekerProfileResponse response = this.profileService.getProfile(currentUser.getId());
        return ApiResponse.success("Profile retrieved successfully", response);
    }

    @Operation(summary = "Update candidate career information, skills, and experience")
    @PutMapping
    public ApiResponse<JobSeekerProfileResponse> updateProfile(@RequestBody JobSeekerProfileRequest request) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        JobSeekerProfileResponse response = this.profileService.updateProfile(request, currentUser.getId());
        return ApiResponse.success("Profile updated successfully", response);
    }

    @Operation(summary = "Upload candidate profile avatar image")
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<JobSeekerProfileResponse> uploadPhoto(@RequestParam("file") MultipartFile file) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        JobSeekerProfileResponse response = this.profileService.uploadProfilePhoto(file, currentUser.getId());
        return ApiResponse.success("Profile photo uploaded successfully", response);
    }
}
