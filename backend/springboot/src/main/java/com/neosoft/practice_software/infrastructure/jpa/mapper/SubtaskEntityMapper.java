package com.neosoft.practice_software.infrastructure.jpa.mapper;

import com.neosoft.practice_software.domain.model.Subtask;
import com.neosoft.practice_software.infrastructure.jpa.entity.SubtaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper for converting between SubtaskEntity and Subtask (Business Object).
 */
@Mapper(componentModel = "spring")
public interface SubtaskEntityMapper {

    /**
     * Convert SubtaskEntity to Subtask.
     *
     * @param entity The entity to convert
     * @return The converted business object
     */
    Subtask toBO(SubtaskEntity entity);

    /**
     * Convert Subtask to SubtaskEntity.
     *
     * @param bo The business object to convert
     * @return The converted entity
     */
    SubtaskEntity toEntity(Subtask bo);

    /**
     * Convert a list of SubtaskEntity to a list of Subtask.
     *
     * @param entities The entities to convert
     * @return The converted business objects
     */
    List<Subtask> toBOs(List<SubtaskEntity> entities);

    /**
     * Update a SubtaskEntity from a Subtask.
     * Ignores id, taskId and createdAt to preserve identity and creation timestamp.
     *
     * @param bo The business object with updated values
     * @param entity The entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromBO(Subtask bo, @MappingTarget SubtaskEntity entity);
}
