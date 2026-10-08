package com.example.ketari.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Employer profile creation/update request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerProfileRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String companyWebsite;
    private String industry;
    private String companyDescription;
    private String logoUrl;
    private String taxId;
}
