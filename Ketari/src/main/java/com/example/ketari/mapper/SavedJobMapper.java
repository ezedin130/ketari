package com.example.ketari.mapper;

import com.example.ketari.dto.job.SavedJobResponse;
import com.example.ketari.model.Job;
import com.example.ketari.model.SavedJob;

/**
 * Mapper for SavedJob entity conversion to SavedJobResponse.
 */
public final class SavedJobMapper {

    private SavedJobMapper() {
    }

    public static SavedJobResponse toResponse(SavedJob savedJob) {
        if (savedJob == null) {
            return null;
        }

        Job job = savedJob.getJob();
        return SavedJobResponse.builder()
                .id(savedJob.getId())
                .jobId(job != null ? job.getId() : null)
                .jobTitle(job != null ? job.getTitle() : null)
                .companyName(job != null ? job.getCompanyName() : null)
                .location(job != null ? job.getLocation() : null)
                .workplaceType(job != null ? job.getWorkplaceType() : null)
                .employmentType(job != null ? job.getEmploymentType() : null)
                .salaryRange(job != null ? job.getSalaryRange() : null)
                .status(job != null ? job.getStatus() : null)
                .savedAt(savedJob.getSavedAt())
                .build();
    }
}
