package com.neosoft.practice_software.infrastructure.jpa.repository;

import com.neosoft.practice_software.infrastructure.jpa.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for TaskEntity.
 */
@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, UUID> {
    
    /**
     * Find tasks by status.
     * 
     * @param status Task status
     * @return List of tasks with the given status
     */
    List<TaskEntity> findByStatus(String status);
    
    /**
     * Find tasks by assignee ID.
     * 
     * @param assigneeId Assignee ID
     * @return List of tasks assigned to the given user
     */
    List<TaskEntity> findByAssigneeId(UUID assigneeId);
    
    /**
     * Find a task by title and assignee ID.
     * 
     * @param title Task title
     * @param assigneeId Assignee ID
     * @return Optional containing the task if found
     */
    Optional<TaskEntity> findByTitleAndAssigneeId(String title, UUID assigneeId);
    
    /**
     * Check if a task exists by title and assignee ID.
     * 
     * @param title Task title
     * @param assigneeId Assignee ID
     * @return true if the task exists
     */
    boolean existsByTitleAndAssigneeId(String title, UUID assigneeId);
} 