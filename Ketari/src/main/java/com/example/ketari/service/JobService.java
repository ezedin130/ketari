package com.example.ketari.service;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.job.JobRequest;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.WorkplaceType;

import java.util.List;

/**
 * Service contract for job postings, searches, and management.
 */
public interface JobService {

    PageResponse<JobResponse> getPublicJobs(
            String keyword,
            String location,
            WorkplaceType workplaceType,
            EmploymentType employmentType,
            String category,
            String experienceLevel,
            String skill,
            int page,
            int size,
            String sortBy,
            String direction
    );

    JobResponse getJobById(Long id);

    PageResponse<JobResponse> getEmployerJobs(Long employerUserId, int page, int size);

    JobResponse createJob(JobRequest request, Long employerUserId);

    JobResponse updateJob(Long id, JobRequest request, Long employerUserId);

    void deleteJob(Long id, Long employerUserId);

    List<String> getCategories();
}
