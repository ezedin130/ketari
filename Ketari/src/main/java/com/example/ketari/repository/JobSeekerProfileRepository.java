package com.example.ketari.repository;

import com.example.ketari.model.JobSeekerProfile;
import com.example.ketari.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for JobSeekerProfile persistence operations.
 */
@Repository
public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Long> {

    Optional<JobSeekerProfile> findByUserId(Long userId);

    Optional<JobSeekerProfile> findByUser(User user);

    boolean existsByUserId(Long userId);
}
