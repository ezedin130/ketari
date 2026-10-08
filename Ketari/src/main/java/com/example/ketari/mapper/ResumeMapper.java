package com.example.ketari.mapper;

import com.example.ketari.dto.resume.ResumeResponse;
import com.example.ketari.model.Resume;

/**
 * Mapper for Resume entity to response DTO conversion.
 */
public final class ResumeMapper {

    private ResumeMapper() {
    }

    public static ResumeResponse toResponse(Resume resume, String downloadBaseUrl) {
        if (resume == null) {
            return null;
        }
        String downloadUrl = (downloadBaseUrl != null ? downloadBaseUrl : "/api/resumes/") + resume.getId() + "/download";
        return ResumeResponse.builder()
                .id(resume.getId())
                .userId(resume.getUser() != null ? resume.getUser().getId() : null)
                .fileName(resume.getFileName())
                .fileType(resume.getFileType())
                .fileSize(resume.getFileSize())
                .downloadUrl(downloadUrl)
                .uploadedAt(resume.getUploadedAt())
                .build();
    }
}
