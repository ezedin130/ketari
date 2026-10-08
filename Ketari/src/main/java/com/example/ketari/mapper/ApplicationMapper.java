package com.example.ketari.mapper;

import com.example.ketari.dto.application.ApplicationResponse;
import com.example.ketari.dto.application.ApplicationStatusHistoryResponse;
import com.example.ketari.model.Application;
import com.example.ketari.model.ApplicationStatusHistory;
import com.example.ketari.model.Job;
import com.example.ketari.model.Resume;
import com.example.ketari.model.User;

/**
 * Mapper for Application and ApplicationStatusHistory conversions.
 */
public final class ApplicationMapper {

    private ApplicationMapper() {
    }

    public static ApplicationResponse toResponse(Application application) {
        if (application == null) {
            return null;
        }

        Job job = application.getJob();
        User applicant = application.getUser();
        Resume resume = application.getResume();

        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(job != null ? job.getId() : null)
                .jobTitle(job != null ? job.getTitle() : null)
                .companyName(job != null ? job.getCompanyName() : null)
                .applicantId(applicant != null ? applicant.getId() : null)
                .applicantName(applicant != null ? applicant.getName() : null)
                .applicantEmail(applicant != null ? applicant.getEmail() : null)
                .resumeId(resume != null ? resume.getId() : null)
                .resumeFileName(resume != null ? resume.getFileName() : null)
                .coverLetter(application.getCoverLetter())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    public static ApplicationStatusHistoryResponse toHistoryResponse(ApplicationStatusHistory history) {
        if (history == null) {
            return null;
        }
        return ApplicationStatusHistoryResponse.builder()
                .id(history.getId())
                .applicationId(history.getApplication() != null ? history.getApplication().getId() : null)
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .note(history.getNote())
                .changedAt(history.getChangedAt())
                .build();
    }
}
