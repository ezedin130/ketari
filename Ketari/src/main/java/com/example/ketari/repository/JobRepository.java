package com.example.ketari.repository;

import com.example.ketari.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Job persistence operations, supporting dynamic specification filtering.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    Page<Job> findByUserId(Long userId, Pageable pageable);

    Page<Job> findByStatus(String status, Pageable pageable);

    Optional<Job> findByIdAndUserId(Long id, Long userId);

    long countByStatus(String status);

    long countByUserId(Long userId);

    List<Job> findByStatusAndApplicationDeadlineBefore(String status, LocalDateTime deadline);
}
