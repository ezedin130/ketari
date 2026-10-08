package com.example.ketari.service.impl;

import com.example.ketari.dto.profile.EmployerProfileRequest;
import com.example.ketari.dto.profile.EmployerProfileResponse;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.mapper.ProfileMapper;
import com.example.ketari.model.EmployerProfile;
import com.example.ketari.model.User;
import com.example.ketari.repository.EmployerProfileRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.EmployerProfileService;
import com.example.ketari.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of EmployerProfileService managing employer company profile and branding.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployerProfileServiceImpl implements EmployerProfileService {

    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public EmployerProfileResponse getProfile(Long userId) {
        EmployerProfile profile = this.employerProfileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));
        return ProfileMapper.toEmployerResponse(profile);
    }

    @Override
    @Transactional
    public EmployerProfileResponse updateProfile(EmployerProfileRequest request, Long userId) {
        EmployerProfile profile = this.employerProfileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));

        ProfileMapper.updateEmployerProfile(profile, request);
        EmployerProfile saved = this.employerProfileRepository.save(profile);
        log.info("Updated employer profile for user: {}", userId);
        return ProfileMapper.toEmployerResponse(saved);
    }

    @Override
    @Transactional
    public EmployerProfileResponse uploadCompanyLogo(MultipartFile file, Long userId) {
        EmployerProfile profile = this.employerProfileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));

        String logoPath = this.fileStorageService.storeFile(file, "logos", userId);
        profile.setLogoUrl(logoPath);

        EmployerProfile saved = this.employerProfileRepository.save(profile);
        log.info("Uploaded company logo for employer user: {}", userId);
        return ProfileMapper.toEmployerResponse(saved);
    }

    private EmployerProfile createDefaultProfile(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        EmployerProfile profile = EmployerProfile.builder()
                .user(user)
                .companyName(user.getName())
                .build();

        return this.employerProfileRepository.save(profile);
    }
}
