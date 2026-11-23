package com.neosoft.practice_software.infrastructure.jpa.repository;

import com.neosoft.practice_software.infrastructure.jpa.entity.SubtaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for SubtaskEntity.
 */
@Repository
public interface JpaSubtaskRepository extends JpaRepository<SubtaskEntity, UUID> {

    /**
     * Find subtasks of a task, ordered by creation order (created_at, id).
     *
     * @param taskId Task ID
     * @return List of subtasks of the task
     */
    List<SubtaskEntity> findByTaskIdOrderByCreatedAtAscIdAsc(UUID taskId);

    /**
     * Find a subtask by its ID and its parent task ID.
     *
     * @param id Subtask ID
     * @param taskId Task ID
     * @return Optional containing the subtask if found
     */
    Optional<SubtaskEntity> findByIdAndTaskId(UUID id, UUID taskId);

    /**
     * Count subtasks of a task.
     *
     * @param taskId Task ID
     * @return Number of subtasks of the task
     */
    long countByTaskId(UUID taskId);
}
