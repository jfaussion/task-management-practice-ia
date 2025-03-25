package com.neosoft.practice_software.application.dao;

import com.neosoft.practice_software.domain.model.Task;
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
    List<Task> findAll();
    
    /**
     * Find tasks by status.
     * 
     * @param status Task status
     * @return List of tasks with the given status
     */
    List<Task> findByStatus(String status);
    
    /**
     * Find tasks by assignee ID.
     * 
     * @param assigneeId Assignee ID
     * @return List of tasks assigned to the given user
     */
    List<Task> findByAssigneeId(UUID assigneeId);
    
    /**
     * Find a task by ID.
     * 
     * @param id Task ID
     * @return Optional containing the task if found
     */
    Optional<Task> findById(UUID id);
    
    /**
     * Find a task by title and assignee ID.
     * 
     * @param title Task title
     * @param assigneeId Assignee ID
     * @return Optional containing the task if found
     */
    Optional<Task> findByTitleAndAssigneeId(String title, UUID assigneeId);
    
    /**
     * Save a task.
     * 
     * @param task Task to save
     * @return Saved task
     */
    Task save(Task task);
    
    /**
     * Update a task.
     * 
     * @param task Task to update
     * @return Updated task
     */
    Task update(Task task);

    
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