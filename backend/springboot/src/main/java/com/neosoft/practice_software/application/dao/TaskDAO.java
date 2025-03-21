package com.neosoft.practice_software.application.dao;

import com.neosoft.practice_software.domain.model.TaskBO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO interface for Task operations.
 */
public interface TaskDAO {
    
    /**
     * Find all tasks.
     * 
     * @return List of all tasks
     */
    List<TaskBO> findAll();
    
    /**
     * Find tasks by status.
     * 
     * @param status Task status
     * @return List of tasks with the given status
     */
    List<TaskBO> findByStatus(String status);
    
    /**
     * Find tasks by assignee ID.
     * 
     * @param assigneeId Assignee ID
     * @return List of tasks assigned to the given user
     */
    List<TaskBO> findByAssigneeId(UUID assigneeId);
    
    /**
     * Find a task by ID.
     * 
     * @param id Task ID
     * @return Optional containing the task if found
     */
    Optional<TaskBO> findById(UUID id);
    
    /**
     * Find a task by title and assignee ID.
     * 
     * @param title Task title
     * @param assigneeId Assignee ID
     * @return Optional containing the task if found
     */
    Optional<TaskBO> findByTitleAndAssigneeId(String title, UUID assigneeId);
    
    /**
     * Save a task.
     * 
     * @param task Task to save
     * @return Saved task
     */
    TaskBO save(TaskBO task);
    
    /**
     * Update a task.
     * 
     * @param task Task to update
     * @return Updated task
     */
    TaskBO update(TaskBO task);
    
    /**
     * Delete a task.
     * 
     * @param id Task ID
     */
    void deleteById(UUID id);
    
    /**
     * Check if a task exists by ID.
     * 
     * @param id Task ID
     * @return true if the task exists
     */
    boolean existsById(UUID id);
    
    /**
     * Check if a task exists by title and assignee ID.
     * 
     * @param title Task title
     * @param assigneeId Assignee ID
     * @return true if the task exists
     */
    boolean existsByTitleAndAssigneeId(String title, UUID assigneeId);
} 