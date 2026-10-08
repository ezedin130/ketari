package com.example.ketari.service;

/**
 * Service contract for background scheduled maintenance and expiration automation.
 */
public interface AutomationService {

    /**
     * Checks and expires open jobs whose application deadlines have passed.
     */
    void expirePassedJobs();
}
