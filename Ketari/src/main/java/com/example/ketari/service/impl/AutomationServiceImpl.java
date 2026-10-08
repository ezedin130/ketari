package com.example.ketari.service.impl;

import com.example.ketari.enums.NotificationType;
import com.example.ketari.model.Job;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.service.AutomationService;
import com.example.ketari.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of AutomationService running periodic background maintenance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutomationServiceImpl implements AutomationService {

    private final JobRepository jobRepository;
    private final NotificationService notificationService;

    @Override
    @Scheduled(cron = "0 0 * * * *") // Run hourly at the top of the hour
    @Transactional
    public void expirePassedJobs() {
        LocalDateTime now = LocalDateTime.now();
        List<Job> expiredJobs = this.jobRepository.findByStatusAndApplicationDeadlineBefore("OPEN", now);

        if (expiredJobs.isEmpty()) {
            log.debug("Automation: No expired jobs found at {}", now);
            return;
        }

        log.info("Automation: Found {} jobs to expire", expiredJobs.size());

        for (Job job : expiredJobs) {
            job.setStatus("EXPIRED");
            this.jobRepository.save(job);

            if (job.getUser() != null) {
                this.notificationService.sendNotification(
                        job.getUser(),
                        NotificationType.JOB_EXPIRED,
                        "Job Posting Expired",
                        "The application deadline for '" + job.getTitle() + "' has passed and the posting is now expired.",
                        job.getId(),
                        "JOB"
                );
            }
        }

        log.info("Automation: Successfully transitioned {} jobs to EXPIRED status", expiredJobs.size());
    }
}
