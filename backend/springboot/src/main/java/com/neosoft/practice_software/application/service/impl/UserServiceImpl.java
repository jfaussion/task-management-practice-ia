package com.neosoft.practice_software.application.service.impl;

import com.neosoft.practice_software.application.dao.UserDAO;
import com.neosoft.practice_software.application.service.UserService;
import com.neosoft.practice_software.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService.
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserDAO userDAO;
    
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        logger.debug("Getting all users with pagination: page={}, size={}",
                    pageable.getPageNumber(), pageable.getPageSize());
        return userDAO.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UUID id) {
        return userDAO.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    @Override
    @Transactional
    public User createUser(User user) {
        // Ensure new user has no ID (will be generated)
        user.setId(null);
        return userDAO.save(user);
    }
    
    @Override
    @Transactional
    public User updateUser(UUID id, User user) {
        // Ensure the ID is set to the path ID
        user.setId(id);
        
        // Check if user exists
        if (!userDAO.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        
        return userDAO.update(user);
    }
    
    @Override
    @Transactional
    public boolean deleteUser(UUID id) {
        // Check if user exists
        if (!userDAO.existsById(id)) {
            return false;
        }
        
        userDAO.deleteById(id);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsersByUsername(String username) {
        // Input validation and sanitization
        if (!StringUtils.hasText(username)) {
            logger.debug("Search term is null or empty, returning empty list");
            return Collections.emptyList();
        }
        
        String trimmedUsername = username.trim();
        if (trimmedUsername.isEmpty()) {
            logger.debug("Search term is empty after trimming, returning empty list");
            return Collections.emptyList();
        }
        
        // Sanitize search term for logging (remove potential log injection characters)
        String sanitizedUsername = trimmedUsername.replaceAll("[\r\n\t]", "_");
        
        logger.debug("Starting user search: term='{}'", sanitizedUsername);
        return userDAO.findByUsernameContainingIgnoreCase(trimmedUsername);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsersByUsername(String username, Pageable pageable) {     
        // Input validation and sanitization
        if (!StringUtils.hasText(username)) {
            logger.debug("Search term is null or empty, returning empty page");
            return Page.empty(pageable);
        }
        
        String trimmedUsername = username.trim();
        if (trimmedUsername.isEmpty()) {
            logger.debug("Search term is empty after trimming, returning empty page");
            return Page.empty(pageable);
        }
        
        // Sanitize search term for logging (remove potential log injection characters)
        String sanitizedUsername = trimmedUsername.replaceAll("[\r\n\t]", "_");
        
        logger.debug("Starting paginated user search: term='{}', page={}, size={}",
                    sanitizedUsername, pageable.getPageNumber(), pageable.getPageSize());  
        return userDAO.findByUsernameContainingIgnoreCase(trimmedUsername, pageable);
    }
}