package com.neosoft.practice_software.application.service.impl;

import com.neosoft.practice_software.application.dao.UserDAO;
import com.neosoft.practice_software.application.service.UserService;
import com.neosoft.practice_software.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService.
 */
@Service
public class UserServiceImpl implements UserService {
    
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
} 