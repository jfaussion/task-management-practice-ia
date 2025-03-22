package com.neosoft.practice_software.application.service;

import com.neosoft.practice_software.domain.model.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Task operations.
 */
public interface TaskService {
    
    /**
     * Get all tasks.
     * 
     * @return List of all tasks
     */
    List<Task> getAllTasks();
    
    /**
     * Get tasks by status.
     * 
     * @param status Task status
     * @return List of tasks with the given status
     */
    List<Task> getTasksByStatus(String status);
    
    /**
     * Get tasks by assignee.
     * 
     * @param assigneeId Assignee ID
     * @return List of tasks assigned to the given user
     */
    List<Task> getTasksByAssignee(UUID assigneeId);
    
    /**
     * Get a task by ID.
     * 
     * @param id Task ID
     * @return Optional containing the task if found
     */
    Optional<Task> getTaskById(UUID id);
    
    /**
     * Create a new task.
     * 
     * @param task Task to create
     * @return Created task
     */
    Task createTask(Task task);
    
    /**
     * Update a task.
     * 
     * @param id Task ID
     * @param task Task data to update
     * @return Updated task
     */
    Task updateTask(UUID id, Task task);
    
    /**
     * Delete a task.
     * 
     * @param id Task ID
     * @return true if the task was deleted
     */
    boolean deleteTask(UUID id);
    
    /**
     * Assign a task to a user.
     * 
     * @param taskId Task ID
     * @param assigneeId Assignee ID (null to unassign)
     * @return Updated task
     */
    Task assignTask(UUID taskId, UUID assigneeId);
    
    /**
     * Update the status of a task.
     * 
     * @param taskId Task ID
     * @param status New status
     * @return Updated task
     */
    Task updateTaskStatus(UUID taskId, String status);
    
    /**
     * Estimate the time required to complete a task.
     * This is a simple decorator method that returns an estimate based on task properties.
     * 
     * @param taskId Task ID
     * @return Estimated time in hours
     */
    double estimateTaskTime(UUID taskId);
} 