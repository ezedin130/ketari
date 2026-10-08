package com.example.ketari.dto.job;

import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.WorkplaceType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Job creation and update request payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    private String companyName;
    private String companyLogoUrl;
    private String location;
    private WorkplaceType workplaceType;
    private EmploymentType employmentType;
    private String salaryRange;
    private String experienceLevel;
    private String category;
    private String skills;
    private String responsibilities;
    private String requirements;
    private String benefits;
    private LocalDateTime applicationDeadline;
    private String status;
}
