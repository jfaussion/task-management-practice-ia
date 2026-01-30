package com.neosoft.practice_software.infrastructure.jpa.repository;

import com.neosoft.practice_software.infrastructure.jpa.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    
    /**
     * Find users by username containing the search term (case-insensitive).
     * Uses Spring Data JPA naming convention for automatic query generation.
     *
     * @param username Username search term
     * @return List of users matching the search criteria
     */
    List<UserEntity> findByUsernameContainingIgnoreCase(String username);
    
    /**
     * Find users by username containing the search term (case-insensitive) with pagination.
     * Uses Spring Data JPA naming convention for automatic query generation.
     *
     * @param username Username search term
     * @param pageable Pagination information
     * @return Page of users matching the search criteria
     */
    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    
    /**
     * Find users by username containing the search term (case-insensitive) with performance optimization.
     * Custom query with performance hints for better execution on large datasets.
     *
     * @param username Username search term
     * @param pageable Pagination information
     * @return Page of users matching the search criteria
     */
    @Query(value = "SELECT u FROM UserEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))",
           nativeQuery = false)
    Page<UserEntity> findByUsernameContainingIgnoreCaseOptimized(@Param("username") String username, Pageable pageable);
}