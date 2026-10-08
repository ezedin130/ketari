package com.example.ketari.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Employer profile response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerProfileResponse {

    private Long id;
    private Long userId;
    private String companyName;
    private String companyWebsite;
    private String industry;
    private String companyDescription;
    private String logoUrl;
    private String taxId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
