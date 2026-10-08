package com.example.ketari.service;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.application.ApplicationRequest;
import com.example.ketari.dto.application.ApplicationResponse;
import com.example.ketari.dto.application.ApplicationStatusHistoryResponse;
import com.example.ketari.dto.application.ApplicationStatusUpdateRequest;

import java.util.List;

/**
 * Service contract for job applications, candidate submissions, status transitions, and audit tracking.
 */
public interface ApplicationService {

    ApplicationResponse apply(ApplicationRequest request, Long applicantUserId);

    PageResponse<ApplicationResponse> getApplicantApplications(Long applicantUserId, int page, int size);

    PageResponse<ApplicationResponse> getEmployerApplications(Long jobId, Long employerUserId, int page, int size);

    ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request, Long employerUserId);

    List<ApplicationStatusHistoryResponse> getApplicationHistory(Long applicationId, Long requestingUserId);

    ApplicationResponse getApplicationById(Long applicationId, Long requestingUserId);
}
