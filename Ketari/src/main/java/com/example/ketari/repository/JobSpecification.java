package com.example.ketari.repository;

import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.WorkplaceType;
import com.example.ketari.model.Job;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Data JPA Specification builder for dynamic multi-criteria Job search and filtering.
 */
public final class JobSpecification {

    private JobSpecification() {
        // Utility class
    }

    public static Specification<Job> filterJobs(
            String keyword,
            String location,
            WorkplaceType workplaceType,
            EmploymentType employmentType,
            String category,
            String experienceLevel,
            String skill,
            String status
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Status filter (defaults to OPEN unless explicitly specified)
            String targetStatus = (status != null && !status.isBlank()) ? status.trim().toUpperCase() : "OPEN";
            predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(root.get("status")), targetStatus));

            // Keyword filter matching title, description, or company name
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
                Predicate descPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
                Predicate compPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), pattern);
                predicates.add(criteriaBuilder.or(titlePredicate, descPredicate, compPredicate));
            }

            // Location filter
            if (location != null && !location.trim().isEmpty()) {
                String pattern = "%" + location.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), pattern));
            }

            // Workplace Type (ON_SITE, HYBRID, REMOTE)
            if (workplaceType != null) {
                predicates.add(criteriaBuilder.equal(root.get("workplaceType"), workplaceType));
            }

            // Employment Type (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP)
            if (employmentType != null) {
                predicates.add(criteriaBuilder.equal(root.get("employmentType"), employmentType));
            }

            // Category filter
            if (category != null && !category.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.trim().toLowerCase()));
            }

            // Experience level filter
            if (experienceLevel != null && !experienceLevel.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("experienceLevel")), experienceLevel.trim().toLowerCase()));
            }

            // Skill filter (searches within the JSON/text representation)
            if (skill != null && !skill.trim().isEmpty()) {
                String pattern = "%" + skill.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("skills")), pattern));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
