package com.neosoft.practice_software.application.service.impl;

import com.neosoft.practice_software.application.dao.SubtaskDAO;
import com.neosoft.practice_software.application.dao.TaskDAO;
import com.neosoft.practice_software.application.service.SubtaskService;
import com.neosoft.practice_software.domain.exception.SubtaskLimitExceededException;
import com.neosoft.practice_software.domain.exception.SubtaskNotFoundException;
import com.neosoft.practice_software.domain.exception.SubtaskValidationException;
import com.neosoft.practice_software.domain.model.Subtask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of SubtaskService.
 */
@Service
public class SubtaskServiceImpl implements SubtaskService {

    private final SubtaskDAO subtaskDAO;
    private final TaskDAO taskDAO;

    public SubtaskServiceImpl(SubtaskDAO subtaskDAO, TaskDAO taskDAO) {
        this.subtaskDAO = subtaskDAO;
        this.taskDAO = taskDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subtask> getSubtasksByTaskId(UUID taskId) {
        requireTaskExists(taskId);
        return subtaskDAO.findByTaskId(taskId);
    }

    @Override
    @Transactional
    public Subtask createSubtask(UUID taskId, String title) {
        String trimmedTitle = validateTitle(title);
        requireTaskExists(taskId);

        // Count and insert in the same transaction so the limit of 50
        // subtasks per task cannot be exceeded concurrently.
        if (subtaskDAO.countByTaskId(taskId) >= MAX_SUBTASKS_PER_TASK) {
            throw new SubtaskLimitExceededException("limite de 50 sous-tâches par tâche atteinte");
        }

        Subtask subtask = new Subtask();
        subtask.setTaskId(taskId);
        subtask.setTitle(trimmedTitle);
        subtask.setDone(false);

        return subtaskDAO.save(subtask);
    }

    @Override
    @Transactional
    public Subtask renameSubtask(UUID taskId, UUID subtaskId, String title) {
        String trimmedTitle = validateTitle(title);
        Subtask subtask = requireSubtaskExists(taskId, subtaskId);

        subtask.setTitle(trimmedTitle);

        return subtaskDAO.update(subtask);
    }

    @Override
    @Transactional
    public Subtask setSubtaskDone(UUID taskId, UUID subtaskId, boolean done) {
        Subtask subtask = requireSubtaskExists(taskId, subtaskId);

        subtask.setDone(done);

        return subtaskDAO.update(subtask);
    }

    @Override
    @Transactional
    public void deleteSubtask(UUID taskId, UUID subtaskId) {
        Subtask subtask = requireSubtaskExists(taskId, subtaskId);

        subtaskDAO.deleteById(subtask.getId());
    }

    private String validateTitle(String title) {
        String trimmed = title == null ? "" : title.trim();
        if (trimmed.isEmpty()) {
            throw new SubtaskValidationException("titre requis (non vide)");
        }
        if (trimmed.length() > MAX_TITLE_LENGTH) {
            throw new SubtaskValidationException("titre limité à 200 caractères");
        }
        return trimmed;
    }

    private void requireTaskExists(UUID taskId) {
        if (!taskDAO.existsById(taskId)) {
            throw new SubtaskNotFoundException("Task not found with ID: " + taskId);
        }
    }

    private Subtask requireSubtaskExists(UUID taskId, UUID subtaskId) {
        return subtaskDAO.findByIdAndTaskId(subtaskId, taskId)
                .orElseThrow(() -> new SubtaskNotFoundException("Subtask not found with ID: " + subtaskId));
    }
}
