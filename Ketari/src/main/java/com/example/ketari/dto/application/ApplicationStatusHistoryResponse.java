package com.example.ketari.dto.application;

import com.example.ketari.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Historical record of an application status change.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusHistoryResponse {

    private Long id;
    private Long applicationId;
    private ApplicationStatus previousStatus;
    private ApplicationStatus newStatus;
    private String note;
    private LocalDateTime changedAt;
}
