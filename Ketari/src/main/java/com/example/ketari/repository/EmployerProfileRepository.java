package com.example.ketari.repository;

import com.example.ketari.model.EmployerProfile;
import com.example.ketari.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for EmployerProfile persistence operations.
 */
@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {

    Optional<EmployerProfile> findByUserId(Long userId);

    Optional<EmployerProfile> findByUser(User user);

    boolean existsByUserId(Long userId);
}
