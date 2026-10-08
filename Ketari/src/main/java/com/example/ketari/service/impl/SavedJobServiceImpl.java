package com.example.ketari.service.impl;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.job.SavedJobResponse;
import com.example.ketari.exception.DuplicateResourceException;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.mapper.SavedJobMapper;
import com.example.ketari.model.Job;
import com.example.ketari.model.SavedJob;
import com.example.ketari.model.User;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.repository.SavedJobRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of SavedJobService managing bookmarked jobs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SavedJobResponse saveJob(Long jobId, Long userId) {
        if (this.savedJobRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new DuplicateResourceException("Job is already bookmarked in your saved jobs list");
        }

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Job job = this.jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        SavedJob savedJob = SavedJob.builder()
                .user(user)
                .job(job)
                .savedAt(LocalDateTime.now())
                .build();

        SavedJob saved = this.savedJobRepository.save(savedJob);
        log.info("User {} bookmarked job ID: {}", userId, jobId);
        return SavedJobMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void removeSavedJob(Long jobId, Long userId) {
        SavedJob savedJob = this.savedJobRepository.findByUserIdAndJobId(userId, jobId)
                .orElseThrow(() -> new ResourceNotFoundException("SavedJob", "jobId", jobId));

        this.savedJobRepository.delete(savedJob);
        log.info("User {} removed bookmark for job ID: {}", userId, jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SavedJobResponse> getSavedJobs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(Sort.Direction.DESC, "savedAt"));
        Page<SavedJob> savedJobPage = this.savedJobRepository.findByUserId(userId, pageable);
        return PageResponse.from(savedJobPage, SavedJobMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isJobSaved(Long jobId, Long userId) {
        return this.savedJobRepository.existsByUserIdAndJobId(userId, jobId);
    }
}
