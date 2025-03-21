package com.neosoft.practice_software.infrastructure.jpa.mapper;

import com.neosoft.practice_software.domain.model.TaskBO;
import com.neosoft.practice_software.infrastructure.jpa.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper for converting between TaskEntity and TaskBO.
 */
@Mapper(componentModel = "spring", uses = {UserEntityMapper.class})
public interface TaskEntityMapper {
    
    /**
     * Convert TaskEntity to TaskBO.
     * 
     * @param entity The entity to convert
     * @return The converted business object
     */
    @Mapping(source = "assignee", target = "assignee")
    TaskBO toBO(TaskEntity entity);
    
    /**
     * Convert TaskBO to TaskEntity.
     * 
     * @param bo The business object to convert
     * @return The converted entity
     */
    TaskEntity toEntity(TaskBO bo);
    
    /**
     * Convert a list of TaskEntity to a list of TaskBO.
     * 
     * @param entities The entities to convert
     * @return The converted business objects
     */
    List<TaskBO> toBOs(List<TaskEntity> entities);
    
    /**
     * Update a TaskEntity from a TaskBO.
     * 
     * @param bo The business object with updated values
     * @param entity The entity to update
     */
    void updateEntityFromBO(TaskBO bo, @MappingTarget TaskEntity entity);
} 