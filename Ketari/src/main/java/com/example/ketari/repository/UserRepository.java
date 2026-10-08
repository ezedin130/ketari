package com.example.ketari.repository;

import com.example.ketari.enums.UserRole;
import com.example.ketari.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User persistence operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByRoleAndIsApproved(UserRole role, Boolean isApproved, Pageable pageable);

    long countByRole(UserRole role);

    long countByRoleAndIsApproved(UserRole role, Boolean isApproved);
}
