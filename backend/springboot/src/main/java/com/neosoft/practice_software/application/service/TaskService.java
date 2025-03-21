package com.neosoft.practice_software.application.service;

import com.neosoft.practice_software.domain.model.TaskBO;
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
    List<TaskBO> getAllTasks();
    
    /**
     * Get tasks by status.
     * 
     * @param status Task status
     * @return List of tasks with the given status
     */
    List<TaskBO> getTasksByStatus(String status);
    
    /**
     * Get tasks by assignee.
     * 
     * @param assigneeId Assignee ID
     * @return List of tasks assigned to the given user
     */
    List<TaskBO> getTasksByAssignee(UUID assigneeId);
    
    /**
     * Get a task by ID.
     * 
     * @param id Task ID
     * @return Optional containing the task if found
     */
    Optional<TaskBO> getTaskById(UUID id);
    
    /**
     * Create a new task.
     * 
     * @param task Task to create
     * @return Created task
     */
    TaskBO createTask(TaskBO task);
    
    /**
     * Update a task.
     * 
     * @param id Task ID
     * @param task Task data to update
     * @return Updated task
     */
    TaskBO updateTask(UUID id, TaskBO task);
    
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
    TaskBO assignTask(UUID taskId, UUID assigneeId);
    
    /**
     * Update the status of a task.
     * 
     * @param taskId Task ID
     * @param status New status
     * @return Updated task
     */
    TaskBO updateTaskStatus(UUID taskId, String status);
    
    /**
     * Estimate the time required to complete a task.
     * This is a simple decorator method that returns an estimate based on task properties.
     * 
     * @param taskId Task ID
     * @return Estimated time in hours
     */
    double estimateTaskTime(UUID taskId);
} 