package com.example.ketari.service.impl;

import com.example.ketari.dto.profile.JobSeekerProfileRequest;
import com.example.ketari.dto.profile.JobSeekerProfileResponse;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.mapper.ProfileMapper;
import com.example.ketari.model.JobSeekerProfile;
import com.example.ketari.model.User;
import com.example.ketari.repository.JobSeekerProfileRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.ProfileService;
import com.example.ketari.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of ProfileService managing candidate resumes and profiles.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final JobSeekerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public JobSeekerProfileResponse getProfile(Long userId) {
        JobSeekerProfile profile = this.profileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));
        return ProfileMapper.toJobSeekerResponse(profile);
    }

    @Override
    @Transactional
    public JobSeekerProfileResponse updateProfile(JobSeekerProfileRequest request, Long userId) {
        JobSeekerProfile profile = this.profileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));

        ProfileMapper.updateJobSeekerProfile(profile, request);
        JobSeekerProfile saved = this.profileRepository.save(profile);
        log.info("Updated candidate profile for user: {}", userId);
        return ProfileMapper.toJobSeekerResponse(saved);
    }

    @Override
    @Transactional
    public JobSeekerProfileResponse uploadProfilePhoto(MultipartFile file, Long userId) {
        JobSeekerProfile profile = this.profileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));

        String photoPath = this.fileStorageService.storeFile(file, "avatars", userId);
        profile.setPhotoUrl(photoPath);

        JobSeekerProfile saved = this.profileRepository.save(profile);
        log.info("Uploaded avatar for candidate user: {}", userId);
        return ProfileMapper.toJobSeekerResponse(saved);
    }

    private JobSeekerProfile createDefaultProfile(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        JobSeekerProfile profile = JobSeekerProfile.builder()
                .user(user)
                .firstName(user.getName())
                .build();

        return this.profileRepository.save(profile);
    }
}
