package com.example.ketari.service.impl;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.job.JobRequest;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.UserRole;
import com.example.ketari.enums.WorkplaceType;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.exception.UnauthorizedException;
import com.example.ketari.mapper.JobMapper;
import com.example.ketari.model.EmployerProfile;
import com.example.ketari.model.Job;
import com.example.ketari.model.User;
import com.example.ketari.repository.ApplicationRepository;
import com.example.ketari.repository.EmployerProfileRepository;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.repository.JobSpecification;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of JobService managing job posts, dynamic searching, and ownership checks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getPublicJobs(
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
    ) {
        String cleanSortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "postedDate";
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(sortDirection, cleanSortBy));

        Specification<Job> spec = JobSpecification.filterJobs(
                keyword,
                location,
                workplaceType,
                employmentType,
                category,
                experienceLevel,
                skill,
                "OPEN"
        );

        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);

        return PageResponse.from(jobPage, job -> {
            long appCount = this.applicationRepository.countByJobId(job.getId());
            return JobMapper.toResponse(job, appCount);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        Job job = this.jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
        long appCount = this.applicationRepository.countByJobId(job.getId());
        return JobMapper.toResponse(job, appCount);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getEmployerJobs(Long employerUserId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "postedDate"));
        Page<Job> jobPage = this.jobRepository.findByUserId(employerUserId, pageable);

        return PageResponse.from(jobPage, job -> {
            long appCount = this.applicationRepository.countByJobId(job.getId());
            return JobMapper.toResponse(job, appCount);
        });
    }

    @Override
    @Transactional
    public JobResponse createJob(JobRequest request, Long employerUserId) {
        User user = this.userRepository.findById(employerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employerUserId));

        if (user.getRole() == UserRole.EMPLOYER && Boolean.FALSE.equals(user.getIsApproved())) {
            throw new UnauthorizedException("Your employer account has not been approved yet");
        }

        Job job = JobMapper.toEntity(request, user);

        // Auto-fill company details from employer profile if not provided in the request
        if (job.getCompanyName() == null || job.getCompanyName().isBlank()) {
            Optional<EmployerProfile> profileOpt = this.employerProfileRepository.findByUserId(employerUserId);
            if (profileOpt.isPresent()) {
                EmployerProfile profile = profileOpt.get();
                job.setCompanyName(profile.getCompanyName());
                if (job.getCompanyLogoUrl() == null || job.getCompanyLogoUrl().isBlank()) {
                    job.setCompanyLogoUrl(profile.getLogoUrl());
                }
            } else {
                job.setCompanyName(user.getName());
            }
        }

        job.setPostedDate(LocalDateTime.now());
        job.setStatus("OPEN");

        Job savedJob = this.jobRepository.save(job);
        log.info("Created job ID: {} by employer: {}", savedJob.getId(), user.getEmail());

        return JobMapper.toResponse(savedJob, 0L);
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long id, JobRequest request, Long employerUserId) {
        Job job = this.jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));

        User user = this.userRepository.findById(employerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employerUserId));

        // Enforce ownership unless admin
        if (user.getRole() != UserRole.ADMIN && (job.getUser() == null || !job.getUser().getId().equals(employerUserId))) {
            throw new UnauthorizedException("You do not have permission to update this job");
        }

        JobMapper.updateEntity(job, request);
        Job updatedJob = this.jobRepository.save(job);
        long appCount = this.applicationRepository.countByJobId(job.getId());

        log.info("Updated job ID: {} by user: {}", updatedJob.getId(), user.getEmail());
        return JobMapper.toResponse(updatedJob, appCount);
    }

    @Override
    @Transactional
    public void deleteJob(Long id, Long employerUserId) {
        Job job = this.jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));

        User user = this.userRepository.findById(employerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employerUserId));

        if (user.getRole() != UserRole.ADMIN && (job.getUser() == null || !job.getUser().getId().equals(employerUserId))) {
            throw new UnauthorizedException("You do not have permission to delete this job");
        }

        this.jobRepository.delete(job);
        log.info("Deleted job ID: {} by user: {}", id, user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return List.of(
                "Engineering",
                "Technology",
                "Data",
                "Design",
                "Marketing",
                "Sales",
                "Finance",
                "Human Resources",
                "Customer Support",
                "Operations",
                "Healthcare",
                "Education"
        );
    }
}
