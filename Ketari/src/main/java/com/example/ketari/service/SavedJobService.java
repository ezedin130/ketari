package com.example.ketari.service;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.job.SavedJobResponse;

/**
 * Service contract for bookmarking / saving job listings.
 */
public interface SavedJobService {

    SavedJobResponse saveJob(Long jobId, Long userId);

    void removeSavedJob(Long jobId, Long userId);

    PageResponse<SavedJobResponse> getSavedJobs(Long userId, int page, int size);

    boolean isJobSaved(Long jobId, Long userId);
}
