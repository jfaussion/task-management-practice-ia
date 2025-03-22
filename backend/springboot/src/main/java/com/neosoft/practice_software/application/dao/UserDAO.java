package com.neosoft.practice_software.application.dao;

import com.neosoft.practice_software.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO interface for User operations.
 */
public interface UserDAO {
    
    /**
     * Find all users.
     * 
     * @return List of all users
     */
    List<User> findAll();
    
    /**
     * Find a user by ID.
     * 
     * @param id User ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(UUID id);
    
    /**
     * Find a user by username.
     * 
     * @param username Username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Save a user.
     * 
     * @param user User to save
     * @return Saved user
     */
    User save(User user);
    
    /**
     * Update a user.
     * 
     * @param user User to update
     * @return Updated user
     */
    User update(User user);
    
    /**
     * Delete a user.
     * 
     * @param id User ID
     */
    void deleteById(UUID id);
    
    /**
     * Check if a user exists by ID.
     * 
     * @param id User ID
     * @return true if the user exists
     */
    boolean existsById(UUID id);
    
    /**
     * Check if a user exists by username.
     * 
     * @param username Username
     * @return true if the user exists
     */
    boolean existsByUsername(String username);
} 