package com.neosoft.practice_software.infrastructure.jpa.dao;

import com.neosoft.practice_software.application.dao.SubtaskDAO;
import com.neosoft.practice_software.domain.model.Subtask;
import com.neosoft.practice_software.infrastructure.jpa.entity.SubtaskEntity;
import com.neosoft.practice_software.infrastructure.jpa.mapper.SubtaskEntityMapper;
import com.neosoft.practice_software.infrastructure.jpa.repository.JpaSubtaskRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of SubtaskDAO.
 */
@Repository
public class SubtaskDAOImpl implements SubtaskDAO {

    private final JpaSubtaskRepository repository;
    private final SubtaskEntityMapper mapper;

    public SubtaskDAOImpl(JpaSubtaskRepository repository, SubtaskEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Subtask> findByTaskId(UUID taskId) {
        return mapper.toBOs(repository.findByTaskIdOrderByCreatedAtAscIdAsc(taskId));
    }

    @Override
    public Optional<Subtask> findByIdAndTaskId(UUID id, UUID taskId) {
        return repository.findByIdAndTaskId(id, taskId).map(mapper::toBO);
    }

    @Override
    public Subtask save(Subtask subtask) {
        SubtaskEntity entity = mapper.toEntity(subtask);
        entity = repository.save(entity);
        return mapper.toBO(entity);
    }

    @Override
    public Subtask update(Subtask subtask) {
        SubtaskEntity existingTask = repository.findById(subtask.getId())
                .orElseThrow(() -> new IllegalArgumentException("Subtask not found with ID: " + subtask.getId()));

        mapper.updateEntityFromBO(subtask, existingTask);

        existingTask = repository.save(existingTask);
        return mapper.toBO(existingTask);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public long countByTaskId(UUID taskId) {
        return repository.countByTaskId(taskId);
    }
}
