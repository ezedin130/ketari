package com.example.ketari.model;

import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.WorkplaceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Job posting persistence entity mapped to 'jobs' table.
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_logo_url")
    private String companyLogoUrl;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "workplace_type", length = 50)
    private WorkplaceType workplaceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 50)
    private EmploymentType employmentType;

    @Column(name = "salary_range")
    private String salaryRange;

    @Column(name = "experience_level", length = 100)
    private String experienceLevel;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "posted_date", nullable = false, updatable = false)
    private LocalDateTime postedDate;

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @Builder.Default
    @Column(name = "status", length = 50)
    private String status = "OPEN";

    @PrePersist
    protected void onPrePersist() {
        if (this.postedDate == null) {
            this.postedDate = LocalDateTime.now();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "OPEN";
        }
    }

    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(this.status);
    }
}
