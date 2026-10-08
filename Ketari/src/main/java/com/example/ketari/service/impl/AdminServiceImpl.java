package com.example.ketari.service.impl;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.admin.PlatformStatsResponse;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.dto.user.UserResponse;
import com.example.ketari.enums.NotificationType;
import com.example.ketari.enums.UserRole;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.mapper.JobMapper;
import com.example.ketari.mapper.UserMapper;
import com.example.ketari.model.Job;
import com.example.ketari.model.User;
import com.example.ketari.repository.ApplicationRepository;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.AdminService;
import com.example.ketari.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AdminService for platform governance and statistics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getPendingEmployers(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "id"));
        Page<User> usersPage = this.userRepository.findByRoleAndIsApproved(UserRole.EMPLOYER, false, pageable);
        return PageResponse.from(usersPage, UserMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse approveEmployer(Long userId, boolean approve) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setIsApproved(approve);
        User savedUser = this.userRepository.save(user);

        if (approve) {
            this.notificationService.sendNotification(
                    savedUser,
                    NotificationType.ACCOUNT_APPROVED,
                    "Account Approved",
                    "Congratulations! Your employer account has been approved by the platform administrators. You can now post jobs.",
                    savedUser.getId(),
                    "USER"
            );
        }

        log.info("Admin updated approval status for employer ID: {} to: {}", userId, approve);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public PlatformStatsResponse getPlatformStats() {
        long totalUsers = this.userRepository.count();
        long totalJobSeekers = this.userRepository.countByRole(UserRole.EMPLOYEE);
        long totalEmployers = this.userRepository.countByRole(UserRole.EMPLOYER);
        long pendingEmployers = this.userRepository.countByRoleAndIsApproved(UserRole.EMPLOYER, false);
        long totalJobs = this.jobRepository.count();
        long openJobs = this.jobRepository.countByStatus("OPEN");
        long totalApplications = this.applicationRepository.count();

        return PlatformStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalJobSeekers(totalJobSeekers)
                .totalEmployers(totalEmployers)
                .pendingEmployers(pendingEmployers)
                .totalJobs(totalJobs)
                .openJobs(openJobs)
                .totalApplications(totalApplications)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getAllJobsAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "id"));
        Page<Job> jobPage = this.jobRepository.findAll(pageable);
        return PageResponse.from(jobPage, job -> {
            long appCount = this.applicationRepository.countByJobId(job.getId());
            return JobMapper.toResponse(job, appCount);
        });
    }

    @Override
    @Transactional
    public void deleteJobAdmin(Long jobId) {
        Job job = this.jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        this.jobRepository.delete(job);
        log.info("Admin deleted job ID: {}", jobId);
    }
}
