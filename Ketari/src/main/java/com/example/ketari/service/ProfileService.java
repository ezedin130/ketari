package com.example.ketari.service;

import com.example.ketari.dto.profile.JobSeekerProfileRequest;
import com.example.ketari.dto.profile.JobSeekerProfileResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service contract for candidate / job seeker profile management.
 */
public interface ProfileService {

    JobSeekerProfileResponse getProfile(Long userId);

    JobSeekerProfileResponse updateProfile(JobSeekerProfileRequest request, Long userId);

    JobSeekerProfileResponse uploadProfilePhoto(MultipartFile file, Long userId);
}
