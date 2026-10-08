package com.example.ketari.service.impl;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.application.ApplicationRequest;
import com.example.ketari.dto.application.ApplicationResponse;
import com.example.ketari.dto.application.ApplicationStatusHistoryResponse;
import com.example.ketari.dto.application.ApplicationStatusUpdateRequest;
import com.example.ketari.enums.ApplicationStatus;
import com.example.ketari.enums.NotificationType;
import com.example.ketari.enums.UserRole;
import com.example.ketari.exception.BadRequestException;
import com.example.ketari.exception.DuplicateResourceException;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.exception.UnauthorizedException;
import com.example.ketari.mapper.ApplicationMapper;
import com.example.ketari.model.Application;
import com.example.ketari.model.ApplicationStatusHistory;
import com.example.ketari.model.Job;
import com.example.ketari.model.Resume;
import com.example.ketari.model.User;
import com.example.ketari.repository.ApplicationRepository;
import com.example.ketari.repository.ApplicationStatusHistoryRepository;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.repository.ResumeRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.ApplicationService;
import com.example.ketari.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of ApplicationService managing application submission,
 * status transitions, history recording, and candidate/employer notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ApplicationResponse apply(ApplicationRequest request, Long applicantUserId) {
        User applicant = this.userRepository.findById(applicantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", applicantUserId));

        Job job = this.jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", request.getJobId()));

        if (!job.isOpen()) {
            throw new BadRequestException("This job posting is currently closed and no longer accepting applications");
        }

        if (this.applicationRepository.existsByUserIdAndJobId(applicantUserId, job.getId())) {
            throw new DuplicateResourceException("You have already applied for this job position");
        }

        Resume resume = this.resumeRepository.findByIdAndUserId(request.getResumeId(), applicantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", request.getResumeId()));

        Application application = Application.builder()
                .user(applicant)
                .job(job)
                .resume(resume)
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Application savedApp = this.applicationRepository.save(application);

        // Record initial history entry
        ApplicationStatusHistory initialHistory = ApplicationStatusHistory.builder()
                .application(savedApp)
                .previousStatus(null)
                .newStatus(ApplicationStatus.APPLIED)
                .note("Application submitted successfully")
                .changedAt(LocalDateTime.now())
                .build();
        this.statusHistoryRepository.save(initialHistory);

        // Notify employer if present
        if (job.getUser() != null) {
            this.notificationService.sendNotification(
                    job.getUser(),
                    NotificationType.NEW_APPLICATION,
                    "New Candidate Application",
                    applicant.getName() + " applied for " + job.getTitle(),
                    savedApp.getId(),
                    "APPLICATION"
            );
        }

        // Notify applicant of successful submission
        this.notificationService.sendNotification(
                applicant,
                NotificationType.APPLICATION_SUBMITTED,
                "Application Submitted",
                "Your application for " + job.getTitle() + " at " + job.getCompanyName() + " was submitted.",
                savedApp.getId(),
                "APPLICATION"
        );

        log.info("Applicant {} successfully applied to job {}", applicant.getEmail(), job.getId());
        return ApplicationMapper.toResponse(savedApp);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getApplicantApplications(Long applicantUserId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = this.applicationRepository.findByUserId(applicantUserId, pageable);
        return PageResponse.from(applicationPage, ApplicationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getEmployerApplications(Long jobId, Long employerUserId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage;

        if (jobId != null) {
            // Verify employer ownership of this job
            Job job = this.jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

            User employer = this.userRepository.findById(employerUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", employerUserId));

            if (employer.getRole() != UserRole.ADMIN && (job.getUser() == null || !job.getUser().getId().equals(employerUserId))) {
                throw new UnauthorizedException("You do not have access to view applications for this job");
            }

            applicationPage = this.applicationRepository.findByJobId(jobId, pageable);
        } else {
            applicationPage = this.applicationRepository.findByJobUserId(employerUserId, pageable);
        }

        return PageResponse.from(applicationPage, ApplicationMapper::toResponse);
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request, Long employerUserId) {
        Application application = this.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        User user = this.userRepository.findById(employerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employerUserId));

        Job job = application.getJob();
        if (user.getRole() != UserRole.ADMIN && (job.getUser() == null || !job.getUser().getId().equals(employerUserId))) {
            throw new UnauthorizedException("You do not have permission to update this application");
        }

        ApplicationStatus previousStatus = application.getStatus();
        ApplicationStatus newStatus = request.getStatus();

        if (previousStatus != newStatus) {
            application.setStatus(newStatus);
            application.setUpdatedAt(LocalDateTime.now());
            Application savedApp = this.applicationRepository.save(application);

            // Audit history
            ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                    .application(savedApp)
                    .previousStatus(previousStatus)
                    .newStatus(newStatus)
                    .note(request.getNote())
                    .changedAt(LocalDateTime.now())
                    .build();
            this.statusHistoryRepository.save(history);

            // Push notification to applicant
            this.notificationService.sendNotification(
                    savedApp.getUser(),
                    NotificationType.STATUS_CHANGED,
                    "Application Status Update",
                    "Your application for " + job.getTitle() + " has been moved to " + newStatus.name().replace('_', ' '),
                    savedApp.getId(),
                    "APPLICATION"
            );

            log.info("Application ID: {} transitioned from {} to {}", applicationId, previousStatus, newStatus);
            return ApplicationMapper.toResponse(savedApp);
        }

        return ApplicationMapper.toResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationStatusHistoryResponse> getApplicationHistory(Long applicationId, Long requestingUserId) {
        Application application = this.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        User user = this.userRepository.findById(requestingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestingUserId));

        boolean isApplicant = application.getUser().getId().equals(requestingUserId);
        boolean isJobOwner = application.getJob().getUser() != null && application.getJob().getUser().getId().equals(requestingUserId);
        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        if (!isApplicant && !isJobOwner && !isAdmin) {
            throw new UnauthorizedException("You do not have permission to view this application history");
        }

        List<ApplicationStatusHistory> historyList = this.statusHistoryRepository.findByApplicationIdOrderByChangedAtAsc(applicationId);
        return historyList.stream().map(ApplicationMapper::toHistoryResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long applicationId, Long requestingUserId) {
        Application application = this.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        User user = this.userRepository.findById(requestingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestingUserId));

        boolean isApplicant = application.getUser().getId().equals(requestingUserId);
        boolean isJobOwner = application.getJob().getUser() != null && application.getJob().getUser().getId().equals(requestingUserId);
        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        if (!isApplicant && !isJobOwner && !isAdmin) {
            throw new UnauthorizedException("You do not have access to this application");
        }

        return ApplicationMapper.toResponse(application);
    }
}
