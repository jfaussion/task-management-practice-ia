package com.neosoft.practice_software.application.dao;

import com.neosoft.practice_software.domain.model.Subtask;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO interface for Subtask operations.
 */
public interface SubtaskDAO {

    /**
     * Find all subtasks of a task, in creation order (created_at, id).
     *
     * @param taskId Task ID
     * @return List of subtasks of the task
     */
    List<Subtask> findByTaskId(UUID taskId);

    /**
     * Find a subtask by ID and parent task ID.
     *
     * @param id Subtask ID
     * @param taskId Task ID
     * @return Optional containing the subtask if found
     */
    Optional<Subtask> findByIdAndTaskId(UUID id, UUID taskId);

    /**
     * Save a subtask.
     *
     * @param subtask Subtask to save
     * @return Saved subtask
     */
    Subtask save(Subtask subtask);

    /**
     * Update a subtask.
     *
     * @param subtask Subtask to update
     * @return Updated subtask
     */
    Subtask update(Subtask subtask);

    /**
     * Delete a subtask by ID.
     *
     * @param id Subtask ID
     */
    void deleteById(UUID id);

    /**
     * Count subtasks of a task.
     *
     * @param taskId Task ID
     * @return Number of subtasks of the task
     */
    long countByTaskId(UUID taskId);
}
