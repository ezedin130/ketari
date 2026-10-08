package com.example.ketari.service.impl;

import com.example.ketari.dto.resume.ResumeResponse;
import com.example.ketari.enums.UserRole;
import com.example.ketari.exception.BadRequestException;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.exception.UnauthorizedException;
import com.example.ketari.mapper.ResumeMapper;
import com.example.ketari.model.Resume;
import com.example.ketari.model.User;
import com.example.ketari.repository.ApplicationRepository;
import com.example.ketari.repository.ResumeRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.ResumeService;
import com.example.ketari.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of ResumeService managing CV file storage and access authorization.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private static final int MAX_RESUMES_PER_USER = 10;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ResumeResponse uploadResume(MultipartFile file, Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        long currentCount = this.resumeRepository.countByUserId(userId);
        if (currentCount >= MAX_RESUMES_PER_USER) {
            throw new BadRequestException("Maximum limit of " + MAX_RESUMES_PER_USER + " resumes reached");
        }

        String storedPath = this.fileStorageService.storeFile(file, "resumes", userId);

        Resume resume = Resume.builder()
                .user(user)
                .fileName(Objects.requireNonNullElse(file.getOriginalFilename(), "resume.pdf"))
                .fileType(Objects.requireNonNullElse(file.getContentType(), "application/octet-stream"))
                .fileSize(file.getSize())
                .storagePath(storedPath)
                .uploadedAt(LocalDateTime.now())
                .build();

        Resume saved = this.resumeRepository.save(resume);
        log.info("Uploaded resume ID: {} for user: {}", saved.getId(), user.getEmail());

        return ResumeMapper.toResponse(saved, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeResponse> getUserResumes(Long userId) {
        return this.resumeRepository.findByUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(r -> ResumeMapper.toResponse(r, null))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(Long id, Long userId) {
        Resume resume = this.resumeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", id));
        return ResumeMapper.toResponse(resume, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadResume(Long resumeId, Long requestingUserId) {
        Resume resume = getResumeEntity(resumeId);

        User requestingUser = this.userRepository.findById(requestingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestingUserId));

        // Access allowed if owner, admin, or an employer reviewing an application using this resume
        boolean isOwner = resume.getUser().getId().equals(requestingUserId);
        boolean isAdmin = requestingUser.getRole() == UserRole.ADMIN;
        boolean isEmployerOfApplicant = false;

        if (!isOwner && !isAdmin && requestingUser.getRole() == UserRole.EMPLOYER) {
            // Check if there is an application on a job belonging to this employer with this resume
            isEmployerOfApplicant = this.applicationRepository.findAll().stream()
                    .anyMatch(app -> app.getResume().getId().equals(resumeId)
                            && app.getJob().getUser() != null
                            && app.getJob().getUser().getId().equals(requestingUserId));
        }

        if (!isOwner && !isAdmin && !isEmployerOfApplicant) {
            throw new UnauthorizedException("You are not authorized to download this resume");
        }

        return this.fileStorageService.loadFileAsResource(resume.getStoragePath());
    }

    @Override
    @Transactional(readOnly = true)
    public Resume getResumeEntity(Long resumeId) {
        return this.resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));
    }

    @Override
    @Transactional
    public void deleteResume(Long resumeId, Long userId) {
        Resume resume = this.resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));

        this.fileStorageService.deleteFile(resume.getStoragePath());
        this.resumeRepository.delete(resume);
        log.info("Deleted resume ID: {} for user: {}", resumeId, userId);
    }
}
