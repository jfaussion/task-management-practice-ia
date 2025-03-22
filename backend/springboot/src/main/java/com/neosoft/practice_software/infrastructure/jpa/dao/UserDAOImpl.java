package com.neosoft.practice_software.infrastructure.jpa.dao;

import com.neosoft.practice_software.application.dao.UserDAO;
import com.neosoft.practice_software.domain.model.User;
import com.neosoft.practice_software.infrastructure.jpa.entity.UserEntity;
import com.neosoft.practice_software.infrastructure.jpa.mapper.UserEntityMapper;
import com.neosoft.practice_software.infrastructure.jpa.repository.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of UserDAO.
 */
@Repository
public class UserDAOImpl implements UserDAO {
    
    private final JpaUserRepository repository;
    private final UserEntityMapper mapper;
    
    public UserDAOImpl(JpaUserRepository repository, UserEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    @Override
    public List<User> findAll() {
        return mapper.toBOs(repository.findAll());
    }
    
    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(mapper::toBO);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(mapper::toBO);
    }
    
    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        
        // Set creation and update dates if not already set
        if (entity.getCreatedAt() == null) {
            LocalDateTime now = LocalDateTime.now();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
        }
        
        entity = repository.save(entity);
        return mapper.toBO(entity);
    }
    
    @Override
    public User update(User user) {
        // First check if the user exists
        Optional<UserEntity> existingUserOpt = repository.findById(user.getId());
        
        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = existingUserOpt.get();
            mapper.updateEntityFromBO(user, existingUser);
            
            // Always update the updatedAt field
            existingUser.setUpdatedAt(LocalDateTime.now());
            
            existingUser = repository.save(existingUser);
            return mapper.toBO(existingUser);
        } else {
            // If the user doesn't exist, just save it as a new one
            return save(user);
        }
    }
    
    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
} 