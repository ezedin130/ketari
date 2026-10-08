package com.example.ketari.repository;

import com.example.ketari.enums.ApplicationStatus;
import com.example.ketari.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Application persistence operations.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findByUserId(Long userId, Pageable pageable);

    Page<Application> findByJobId(Long jobId, Pageable pageable);

    Page<Application> findByJobUserId(Long employerUserId, Pageable pageable);

    Page<Application> findByJobIdAndJobUserId(Long jobId, Long employerUserId, Pageable pageable);

    Optional<Application> findByIdAndUserId(Long id, Long userId);

    Optional<Application> findByIdAndJobUserId(Long id, Long employerUserId);

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    long countByJobId(Long jobId);

    long countByJobUserId(Long employerUserId);

    long countByUserId(Long userId);

    long countByStatus(ApplicationStatus status);
}
