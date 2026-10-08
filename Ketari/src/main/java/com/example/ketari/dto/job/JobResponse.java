package com.example.ketari.dto.job;

import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.WorkplaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Public response representation of a Job.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private Long userId;
    private String title;
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
    private LocalDateTime postedDate;
    private LocalDateTime applicationDeadline;
    private String status;
    private Long applicationCount;
}
