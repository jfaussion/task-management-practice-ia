package com.neosoft.practice_software.application.service;

import com.neosoft.practice_software.domain.model.Subtask;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Subtask operations.
 */
public interface SubtaskService {

    /**
     * Maximum number of subtasks allowed per task.
     */
    int MAX_SUBTASKS_PER_TASK = 50;

    /**
     * Maximum length of a subtask title.
     */
    int MAX_TITLE_LENGTH = 200;

    /**
     * Get all subtasks of a task, in creation order (created_at, id).
     *
     * @param taskId Task ID
     * @return List of subtasks of the task
     * @throws com.neosoft.practice_software.domain.exception.SubtaskNotFoundException if the task does not exist
     */
    List<Subtask> getSubtasksByTaskId(UUID taskId);

    /**
     * Create a subtask for a task.
     *
     * @param taskId Task ID
     * @param title Subtask title (trimmed, non-empty, at most 200 characters)
     * @return Created subtask
     * @throws com.neosoft.practice_software.domain.exception.SubtaskValidationException if the title is invalid
     * @throws com.neosoft.practice_software.domain.exception.SubtaskNotFoundException if the task does not exist
     * @throws com.neosoft.practice_software.domain.exception.SubtaskLimitExceededException if the task already has 50 subtasks
     */
    Subtask createSubtask(UUID taskId, String title);

    /**
     * Rename a subtask.
     *
     * @param taskId Task ID
     * @param subtaskId Subtask ID
     * @param title New title (trimmed, non-empty, at most 200 characters)
     * @return Updated subtask
     * @throws com.neosoft.practice_software.domain.exception.SubtaskValidationException if the title is invalid
     * @throws com.neosoft.practice_software.domain.exception.SubtaskNotFoundException if the task or subtask does not exist
     */
    Subtask renameSubtask(UUID taskId, UUID subtaskId, String title);

    /**
     * Check / uncheck a subtask.
     *
     * @param taskId Task ID
     * @param subtaskId Subtask ID
     * @param done New done state
     * @return Updated subtask
     * @throws com.neosoft.practice_software.domain.exception.SubtaskNotFoundException if the task or subtask does not exist
     */
    Subtask setSubtaskDone(UUID taskId, UUID subtaskId, boolean done);

    /**
     * Delete a subtask.
     *
     * @param taskId Task ID
     * @param subtaskId Subtask ID
     * @throws com.neosoft.practice_software.domain.exception.SubtaskNotFoundException if the task or subtask does not exist
     */
    void deleteSubtask(UUID taskId, UUID subtaskId);
}
