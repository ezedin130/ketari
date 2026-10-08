package com.example.ketari.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Saved/Bookmarked job entity mapped to 'saved_jobs' table.
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = {"user", "job"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "saved_jobs",
    uniqueConstraints = @UniqueConstraint(name = "user_job_unique", columnNames = {"user_id", "job_id"})
)
public class SavedJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onPrePersist() {
        if (this.savedAt == null) {
            this.savedAt = LocalDateTime.now();
        }
    }
}
