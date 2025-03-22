package com.neosoft.practice_software.infrastructure.jpa.repository;

import com.neosoft.practice_software.infrastructure.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for UserEntity.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    
    /**
     * Find a user by username.
     * 
     * @param username Username
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByUsername(String username);
    
    /**
     * Check if a user exists by username.
     * 
     * @param username Username
     * @return true if the user exists
     */
    boolean existsByUsername(String username);
} 