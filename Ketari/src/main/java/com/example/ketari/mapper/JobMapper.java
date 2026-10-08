package com.example.ketari.mapper;

import com.example.ketari.dto.job.JobRequest;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.model.Job;
import com.example.ketari.model.User;

/**
 * Mapper for Job entity, request, and response transformations.
 */
public final class JobMapper {

    private JobMapper() {
    }

    public static Job toEntity(JobRequest request, User user) {
        if (request == null) {
            return null;
        }
        return Job.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .companyName(request.getCompanyName())
                .companyLogoUrl(request.getCompanyLogoUrl())
                .location(request.getLocation())
                .workplaceType(request.getWorkplaceType())
                .employmentType(request.getEmploymentType())
                .salaryRange(request.getSalaryRange())
                .experienceLevel(request.getExperienceLevel())
                .category(request.getCategory())
                .skills(request.getSkills())
                .responsibilities(request.getResponsibilities())
                .requirements(request.getRequirements())
                .benefits(request.getBenefits())
                .applicationDeadline(request.getApplicationDeadline())
                .status((request.getStatus() != null && !request.getStatus().isBlank()) ? request.getStatus() : "OPEN")
                .build();
    }

    public static void updateEntity(Job job, JobRequest request) {
        if (job == null || request == null) {
            return;
        }
        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getCompanyName() != null) job.setCompanyName(request.getCompanyName());
        if (request.getCompanyLogoUrl() != null) job.setCompanyLogoUrl(request.getCompanyLogoUrl());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getWorkplaceType() != null) job.setWorkplaceType(request.getWorkplaceType());
        if (request.getEmploymentType() != null) job.setEmploymentType(request.getEmploymentType());
        if (request.getSalaryRange() != null) job.setSalaryRange(request.getSalaryRange());
        if (request.getExperienceLevel() != null) job.setExperienceLevel(request.getExperienceLevel());
        if (request.getCategory() != null) job.setCategory(request.getCategory());
        if (request.getSkills() != null) job.setSkills(request.getSkills());
        if (request.getResponsibilities() != null) job.setResponsibilities(request.getResponsibilities());
        if (request.getRequirements() != null) job.setRequirements(request.getRequirements());
        if (request.getBenefits() != null) job.setBenefits(request.getBenefits());
        if (request.getApplicationDeadline() != null) job.setApplicationDeadline(request.getApplicationDeadline());
        if (request.getStatus() != null && !request.getStatus().isBlank()) job.setStatus(request.getStatus());
    }

    public static JobResponse toResponse(Job job) {
        return toResponse(job, 0L);
    }

    public static JobResponse toResponse(Job job, Long applicationCount) {
        if (job == null) {
            return null;
        }
        return JobResponse.builder()
                .id(job.getId())
                .userId(job.getUser() != null ? job.getUser().getId() : null)
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompanyName())
                .companyLogoUrl(job.getCompanyLogoUrl())
                .location(job.getLocation())
                .workplaceType(job.getWorkplaceType())
                .employmentType(job.getEmploymentType())
                .salaryRange(job.getSalaryRange())
                .experienceLevel(job.getExperienceLevel())
                .category(job.getCategory())
                .skills(job.getSkills())
                .responsibilities(job.getResponsibilities())
                .requirements(job.getRequirements())
                .benefits(job.getBenefits())
                .postedDate(job.getPostedDate())
                .applicationDeadline(job.getApplicationDeadline())
                .status(job.getStatus())
                .applicationCount(applicationCount != null ? applicationCount : 0L)
                .build();
    }
}
