package com.neosoft.practice_software.infrastructure.jpa.dao;

import com.neosoft.practice_software.application.dao.TaskDAO;
import com.neosoft.practice_software.domain.model.Task;
import com.neosoft.practice_software.infrastructure.jpa.entity.TaskEntity;
import com.neosoft.practice_software.infrastructure.jpa.mapper.TaskEntityMapper;
import com.neosoft.practice_software.infrastructure.jpa.repository.JpaTaskRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of TaskDAO.
 */
@Repository
public class TaskDAOImpl implements TaskDAO {
    
    private final JpaTaskRepository repository;
    private final TaskEntityMapper mapper;
    
    public TaskDAOImpl(JpaTaskRepository repository, TaskEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    @Override
    public List<Task> findAll() {
        return mapper.toBOs(repository.findAll());
    }
    
    @Override
    public List<Task> findByStatus(String status) {
        return mapper.toBOs(repository.findByStatus(status));
    }
    
    @Override
    public List<Task> findByAssigneeId(UUID assigneeId) {
        return mapper.toBOs(repository.findByAssigneeId(assigneeId));
    }
    
    @Override
    public Optional<Task> findById(UUID id) {
        return repository.findById(id).map(mapper::toBO);
    }
    
    @Override
    public Optional<Task> findByTitleAndAssigneeId(String title, UUID assigneeId) {
        return repository.findByTitleAndAssigneeId(title, assigneeId).map(mapper::toBO);
    }
    
    @Override
    public Task save(Task task) {
        TaskEntity entity = mapper.toEntity(task);
        
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
    public Task update(Task task) {
        // First check if the task exists
        Optional<TaskEntity> existingTaskOpt = repository.findById(task.getId());
        
        if (existingTaskOpt.isPresent()) {
            TaskEntity existingTask = existingTaskOpt.get();
            mapper.updateEntityFromBO(task, existingTask);
            
            // Always update the updatedAt field
            existingTask.setUpdatedAt(LocalDateTime.now());
            
            existingTask = repository.save(existingTask);
            return mapper.toBO(existingTask);
        } else {
            // If the task doesn't exist, just save it as a new one
            return save(task);
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
    public boolean existsByTitleAndAssigneeId(String title, UUID assigneeId) {
        return repository.existsByTitleAndAssigneeId(title, assigneeId);
    }
} 