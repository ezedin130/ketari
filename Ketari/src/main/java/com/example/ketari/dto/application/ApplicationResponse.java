package com.example.ketari.dto.application;

import com.example.ketari.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Detailed application response payload including candidate and job info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;

    // Candidate details
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;

    // Resume details
    private Long resumeId;
    private String resumeFileName;

    private String coverLetter;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
