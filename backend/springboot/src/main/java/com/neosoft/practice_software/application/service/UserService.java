package com.neosoft.practice_software.application.service;

import com.neosoft.practice_software.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for User operations.
 */
public interface UserService {
    
    /**
     * Get all users.
     * 
     * @return List of all users
     */
    List<User> getAllUsers();
    
    /**
     * Get a user by ID.
     * 
     * @param id User ID
     * @return Optional containing the user if found
     */
    Optional<User> getUserById(UUID id);
    
    /**
     * Get a user by username.
     * 
     * @param username Username
     * @return Optional containing the user if found
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * Create a new user.
     * 
     * @param user User to create
     * @return Created user
     */
    User createUser(User user);
    
    /**
     * Update a user.
     * 
     * @param id User ID
     * @param user User data to update
     * @return Updated user
     */
    User updateUser(UUID id, User user);
    
    /**
     * Delete a user.
     * 
     * @param id User ID
     * @return true if the user was deleted
     */
    boolean deleteUser(UUID id);
    
    /**
     * Check if a username already exists.
     * 
     * @param username Username to check
     * @return true if the username exists
     */
    boolean existsByUsername(String username);
} 