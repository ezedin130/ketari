package com.example.ketari.service;

import com.example.ketari.dto.resume.ResumeResponse;
import com.example.ketari.model.Resume;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service contract for candidate resume uploads, downloads, and lifecycle management.
 */
public interface ResumeService {

    ResumeResponse uploadResume(MultipartFile file, Long userId);

    List<ResumeResponse> getUserResumes(Long userId);

    ResumeResponse getResumeById(Long id, Long userId);

    Resource downloadResume(Long resumeId, Long requestingUserId);

    Resume getResumeEntity(Long resumeId);

    void deleteResume(Long resumeId, Long userId);
}
