package com.example.ketari.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Candidate profile creation/update request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerProfileRequest {

    private String firstName;
    private String lastName;
    private String photoUrl;
    private String bio;
    private String location;
    private String phoneNumber;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String skills;
    private String education;
    private String workExperience;
    private String certifications;
    private String languages;
}
