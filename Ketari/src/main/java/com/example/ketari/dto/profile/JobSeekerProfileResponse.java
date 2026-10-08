package com.example.ketari.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Candidate profile response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerProfileResponse {

    private Long id;
    private Long userId;
    private String email;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
