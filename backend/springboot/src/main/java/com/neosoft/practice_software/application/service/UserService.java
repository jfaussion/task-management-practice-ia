package com.neosoft.practice_software.application.service;

import com.neosoft.practice_software.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    
    /**
     * Search users by username using case-insensitive partial matching.
     *
     * <p>This method performs a case-insensitive search for users whose username
     * contains the provided search term. The search uses partial matching, meaning
     * it will find users whose username contains the search term anywhere within it.</p>
     *
     * <p>For example, searching for "john" will match usernames like "john_doe",
     * "JOHN123", "alice_john", etc.</p>
     *
     * <p>Edge cases:</p>
     * <ul>
     *   <li>If the search term is null or empty after trimming, an empty list is returned</li>
     *   <li>Search terms shorter than 2 characters will throw a validation exception</li>
     *   <li>Search terms longer than 50 characters will throw a validation exception</li>
     * </ul>
     *
     * @param username The username search term (case-insensitive, partial matching).
     *                 Must be between 2 and 50 characters long and not blank.
     * @return List of users whose username contains the search term (case-insensitive).
     *         Returns an empty list if no matches are found.
     * @throws jakarta.validation.ConstraintViolationException if username validation fails
     */
    List<User> searchUsersByUsername(@NotBlank @Size(min = 2, max = 50) String username);
    
    /**
     * Search users by username using case-insensitive partial matching with pagination.
     *
     * <p>This method performs a case-insensitive search for users whose username
     * contains the provided search term, with support for pagination and sorting.
     * The search uses partial matching, meaning it will find users whose username
     * contains the search term anywhere within it.</p>
     *
     * <p>For example, searching for "john" will match usernames like "john_doe",
     * "JOHN123", "alice_john", etc.</p>
     *
     * <p>Pagination allows you to:</p>
     * <ul>
     *   <li>Limit the number of results per page</li>
     *   <li>Navigate through multiple pages of results</li>
     *   <li>Sort results by any User field (e.g., username, createdAt)</li>
     * </ul>
     *
     * <p>Edge cases:</p>
     * <ul>
     *   <li>If the search term is null or empty after trimming, an empty page is returned</li>
     *   <li>Search terms shorter than 2 characters will throw a validation exception</li>
     *   <li>Search terms longer than 50 characters will throw a validation exception</li>
     *   <li>If the requested page number exceeds available pages, an empty page is returned</li>
     * </ul>
     *
     * @param username The username search term (case-insensitive, partial matching).
     *                 Must be between 2 and 50 characters long and not blank.
     * @param pageable Pagination and sorting information (page number, size, sort criteria)
     * @return Page containing users whose username contains the search term (case-insensitive).
     *         Returns an empty page if no matches are found or if the page number exceeds available pages.
     * @throws jakarta.validation.ConstraintViolationException if username validation fails
     */
    Page<User> searchUsersByUsername(@NotBlank @Size(min = 2, max = 50) String username, Pageable pageable);
}