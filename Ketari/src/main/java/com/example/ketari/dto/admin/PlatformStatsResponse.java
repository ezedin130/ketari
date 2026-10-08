package com.example.ketari.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * System-wide administrative platform metrics response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformStatsResponse {

    private long totalUsers;
    private long totalJobSeekers;
    private long totalEmployers;
    private long pendingEmployers;
    private long totalJobs;
    private long openJobs;
    private long totalApplications;
}
