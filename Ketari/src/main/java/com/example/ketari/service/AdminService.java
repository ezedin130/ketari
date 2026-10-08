package com.example.ketari.service;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.admin.PlatformStatsResponse;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.dto.user.UserResponse;

/**
 * Service contract for platform administrator governance, employer vetting, and analytics.
 */
public interface AdminService {

    PageResponse<UserResponse> getPendingEmployers(int page, int size);

    UserResponse approveEmployer(Long userId, boolean approve);

    PlatformStatsResponse getPlatformStats();

    PageResponse<JobResponse> getAllJobsAdmin(int page, int size);

    void deleteJobAdmin(Long jobId);
}
