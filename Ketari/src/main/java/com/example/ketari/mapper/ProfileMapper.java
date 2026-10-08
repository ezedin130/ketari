package com.example.ketari.mapper;

import com.example.ketari.dto.profile.EmployerProfileRequest;
import com.example.ketari.dto.profile.EmployerProfileResponse;
import com.example.ketari.dto.profile.JobSeekerProfileRequest;
import com.example.ketari.dto.profile.JobSeekerProfileResponse;
import com.example.ketari.model.EmployerProfile;
import com.example.ketari.model.JobSeekerProfile;
import com.example.ketari.model.User;

/**
 * Mapper for JobSeekerProfile and EmployerProfile conversions.
 */
public final class ProfileMapper {

    private ProfileMapper() {
    }

    public static JobSeekerProfileResponse toJobSeekerResponse(JobSeekerProfile profile) {
        if (profile == null) {
            return null;
        }
        User user = profile.getUser();
        return JobSeekerProfileResponse.builder()
                .id(profile.getId())
                .userId(user != null ? user.getId() : null)
                .email(user != null ? user.getEmail() : null)
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .photoUrl(profile.getPhotoUrl())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .phoneNumber(profile.getPhoneNumber())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .portfolioUrl(profile.getPortfolioUrl())
                .skills(profile.getSkills())
                .education(profile.getEducation())
                .workExperience(profile.getWorkExperience())
                .certifications(profile.getCertifications())
                .languages(profile.getLanguages())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public static void updateJobSeekerProfile(JobSeekerProfile profile, JobSeekerProfileRequest request) {
        if (profile == null || request == null) {
            return;
        }
        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getPhotoUrl() != null) profile.setPhotoUrl(request.getPhotoUrl());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getPhoneNumber() != null) profile.setPhoneNumber(request.getPhoneNumber());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());
        if (request.getEducation() != null) profile.setEducation(request.getEducation());
        if (request.getWorkExperience() != null) profile.setWorkExperience(request.getWorkExperience());
        if (request.getCertifications() != null) profile.setCertifications(request.getCertifications());
        if (request.getLanguages() != null) profile.setLanguages(request.getLanguages());
    }

    public static EmployerProfileResponse toEmployerResponse(EmployerProfile profile) {
        if (profile == null) {
            return null;
        }
        User user = profile.getUser();
        return EmployerProfileResponse.builder()
                .id(profile.getId())
                .userId(user != null ? user.getId() : null)
                .companyName(profile.getCompanyName())
                .companyWebsite(profile.getCompanyWebsite())
                .industry(profile.getIndustry())
                .companyDescription(profile.getCompanyDescription())
                .logoUrl(profile.getLogoUrl())
                .taxId(profile.getTaxId())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public static void updateEmployerProfile(EmployerProfile profile, EmployerProfileRequest request) {
        if (profile == null || request == null) {
            return;
        }
        if (request.getCompanyName() != null) profile.setCompanyName(request.getCompanyName());
        if (request.getCompanyWebsite() != null) profile.setCompanyWebsite(request.getCompanyWebsite());
        if (request.getIndustry() != null) profile.setIndustry(request.getIndustry());
        if (request.getCompanyDescription() != null) profile.setCompanyDescription(request.getCompanyDescription());
        if (request.getLogoUrl() != null) profile.setLogoUrl(request.getLogoUrl());
        if (request.getTaxId() != null) profile.setTaxId(request.getTaxId());
    }
}
