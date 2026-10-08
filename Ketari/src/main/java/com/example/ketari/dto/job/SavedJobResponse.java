package com.example.ketari.dto.job;

import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.WorkplaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Saved/Bookmarked job response payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private WorkplaceType workplaceType;
    private EmploymentType employmentType;
    private String salaryRange;
    private String status;
    private LocalDateTime savedAt;
}
