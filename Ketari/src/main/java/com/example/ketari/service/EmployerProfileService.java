package com.example.ketari.service;

import com.example.ketari.dto.profile.EmployerProfileRequest;
import com.example.ketari.dto.profile.EmployerProfileResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service contract for employer company profiles and branding management.
 */
public interface EmployerProfileService {

    EmployerProfileResponse getProfile(Long userId);

    EmployerProfileResponse updateProfile(EmployerProfileRequest request, Long userId);

    EmployerProfileResponse uploadCompanyLogo(MultipartFile file, Long userId);
}
