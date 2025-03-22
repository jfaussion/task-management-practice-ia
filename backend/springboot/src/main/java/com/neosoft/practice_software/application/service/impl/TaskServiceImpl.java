package com.neosoft.practice_software.application.service.impl;

import com.neosoft.practice_software.application.dao.TaskDAO;
import com.neosoft.practice_software.application.dao.UserDAO;
import com.neosoft.practice_software.application.service.TaskService;
import com.neosoft.practice_software.domain.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of TaskService.
 */
@Service
public class TaskServiceImpl implements TaskService {
    
    private final TaskDAO taskDAO;
    private final UserDAO userDAO;
    
    public TaskServiceImpl(TaskDAO taskDAO, UserDAO userDAO) {
        this.taskDAO = taskDAO;
        this.userDAO = userDAO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskDAO.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(String status) {
        return taskDAO.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByAssignee(UUID assigneeId) {
        // Verify that the user exists
        if (!userDAO.existsById(assigneeId)) {
            throw new IllegalArgumentException("User not found with ID: " + assigneeId);
        }
        
        return taskDAO.findByAssigneeId(assigneeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(UUID id) {
        return taskDAO.findById(id);
    }
    
    @Override
    @Transactional
    public Task createTask(Task task) {
        // Ensure new task has no ID (will be generated)
        task.setId(null);
        
        // Verify that the assignee exists if provided
        UUID assigneeId = task.getAssigneeId();
        if (assigneeId != null && !userDAO.existsById(assigneeId)) {
            throw new IllegalArgumentException("Assignee not found with ID: " + assigneeId);
        }
        
        // Check for title uniqueness per assignee
        if (assigneeId != null && task.getTitle() != null && 
            taskDAO.existsByTitleAndAssigneeId(task.getTitle(), assigneeId)) {
            throw new IllegalArgumentException("A task with this title already exists for this user");
        }
        
        return taskDAO.save(task);
    }
    
    @Override
    @Transactional
    public Task updateTask(UUID id, Task task) {
        // Ensure the ID is set to the path ID
        task.setId(id);
        
        // Check if task exists
        if (!taskDAO.existsById(id)) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }
        
        // Verify that the assignee exists if provided
        UUID assigneeId = task.getAssigneeId();
        if (assigneeId != null && !userDAO.existsById(assigneeId)) {
            throw new IllegalArgumentException("Assignee not found with ID: " + assigneeId);
        }
        
        // Get the original task to check if title or assignee changed
        Optional<Task> originalTaskOpt = taskDAO.findById(id);
        if (originalTaskOpt.isPresent()) {
            Task originalTask = originalTaskOpt.get();
            
            // Check for title uniqueness per assignee if title or assignee changed
            if (assigneeId != null && task.getTitle() != null && 
                (assigneeId != originalTask.getAssigneeId() || !task.getTitle().equals(originalTask.getTitle())) &&
                taskDAO.existsByTitleAndAssigneeId(task.getTitle(), assigneeId)) {
                throw new IllegalArgumentException("A task with this title already exists for this user");
            }
        }
        
        return taskDAO.update(task);
    }
    
    @Override
    @Transactional
    public boolean deleteTask(UUID id) {
        // Check if task exists
        if (!taskDAO.existsById(id)) {
            return false;
        }
        
        taskDAO.deleteById(id);
        return true;
    }
    
    @Override
    @Transactional
    public Task assignTask(UUID taskId, UUID assigneeId) {
        // Get the task
        Task task = taskDAO.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Verify that the assignee exists if provided
        if (assigneeId != null && !userDAO.existsById(assigneeId)) {
            throw new IllegalArgumentException("Assignee not found with ID: " + assigneeId);
        }
        
        // Check for title uniqueness per assignee if assignee changed
        if (assigneeId != null && task.getTitle() != null && 
            !assigneeId.equals(task.getAssigneeId()) &&
            taskDAO.existsByTitleAndAssigneeId(task.getTitle(), assigneeId)) {
            throw new IllegalArgumentException("A task with this title already exists for this user");
        }
        
        // Set the new assignee
        task.setAssigneeId(assigneeId);
        
        return taskDAO.update(task);
    }
    
    @Override
    @Transactional
    public Task updateTaskStatus(UUID taskId, String status) {
        // Get the task
        Task task = taskDAO.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Validate status (should be done with an enum in a real application)
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        
        // Set the new status
        task.setStatus(status);
        
        return taskDAO.update(task);
    }
    
    @Override
    @Transactional(readOnly = true)
    public double estimateTaskTime(UUID taskId) {
        // Get the task
        Task task = taskDAO.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Simple estimation logic based on task properties
        double baseHours = 2.0; // Default base hours
        
        // Adjust based on priority
        if (task.getPriority() != null) {
            switch (task.getPriority()) {
                case "HIGH":
                    baseHours *= 1.5;
                    break;
                case "LOW":
                    baseHours *= 0.75;
                    break;
                default: // MEDIUM or other
                    // Keep base hours
                    break;
            }
        }
        
        // Adjust based on description length (if available)
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            // Longer descriptions might indicate more complex tasks
            int wordCount = task.getDescription().split("\\s+").length;
            baseHours += (wordCount / 50) * 0.5; // Add 0.5 hours per 50 words
        }
        
        // Adjust based on status
        if ("IN_PROGRESS".equals(task.getStatus())) {
            baseHours *= 0.7; // 30% already done
        } else if ("DONE".equals(task.getStatus())) {
            baseHours = 0; // Already completed
        }
        
        return Math.max(0.25, baseHours); // Minimum 15 minutes (0.25 hours)
    }
    
    /**
     * Validate task status.
     * 
     * @param status Status to validate
     * @return true if the status is valid
     */
    private boolean isValidStatus(String status) {
        return "TODO".equals(status) || "IN_PROGRESS".equals(status) || "DONE".equals(status);
    }
} 