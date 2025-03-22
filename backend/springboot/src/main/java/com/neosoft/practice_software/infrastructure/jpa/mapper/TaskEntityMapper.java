package com.neosoft.practice_software.infrastructure.jpa.mapper;

import com.neosoft.practice_software.domain.model.Task;
import com.neosoft.practice_software.infrastructure.jpa.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper for converting between TaskEntity and Task (Business Object).
 */
@Mapper(componentModel = "spring", uses = {UserEntityMapper.class})
public interface TaskEntityMapper {
    
    /**
     * Convert TaskEntity to Task.
     * 
     * @param entity The entity to convert
     * @return The converted business object
     */
    @Mapping(source = "assignee", target = "assignee")
    Task toBO(TaskEntity entity);
    
    /**
     * Convert Task to TaskEntity.
     * 
     * @param bo The business object to convert
     * @return The converted entity
     */
    TaskEntity toEntity(Task bo);
    
    /**
     * Convert a list of TaskEntity to a list of Task.
     * 
     * @param entities The entities to convert
     * @return The converted business objects
     */
    List<Task> toBOs(List<TaskEntity> entities);
    
    /**
     * Update a TaskEntity from a Task.
     * 
     * @param bo The business object with updated values
     * @param entity The entity to update
     */
    void updateEntityFromBO(Task bo, @MappingTarget TaskEntity entity);
} 