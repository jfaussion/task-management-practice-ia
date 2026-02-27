package com.neosoft.practice_software.infrastructure.api.mapper;

import com.neosoft.practice_software.domain.model.Task;
import com.neosoft.practice_software.infrastructure.api.dto.CreateTaskDTO;
import com.neosoft.practice_software.infrastructure.api.dto.TaskDTO;
import com.neosoft.practice_software.infrastructure.api.dto.UpdateTaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper for converting between TaskDTO and Task (Business Object).
 */
@Mapper(componentModel = "spring", uses = {UserDTOMapper.class})
public interface TaskDTOMapper {
    
    /**
     * Convert TaskBO to TaskDTO.
     * 
     * @param bo The business object to convert
     * @return The converted DTO
     */
    @Mapping(source = "assignee", target = "assignee")
    TaskDTO toDTO(Task bo);
    
    /**
     * Convert TaskDTO to TaskBO.
     * 
     * @param dto The DTO to convert
     * @return The converted business object
     */
    Task toBO(TaskDTO dto);
    
    /**
     * Convert CreateTaskDTO to TaskBO.
     *
     * @param dto The DTO to convert
     * @return The converted business object
     */
    Task toBO(CreateTaskDTO dto);

    /**
     * Convert UpdateTaskDTO to TaskBO.
     *
     * @param dto The DTO to convert
     * @return The converted business object
     */
    Task toBO(UpdateTaskDTO dto);

    /**
     * Convert a list of TaskBO to a list of TaskDTO.
     *
     * @param bos The business objects to convert
     * @return The converted DTOs
     */
    List<TaskDTO> toDTOs(List<Task> bos);
    
    /**
     * Update a TaskBO from a TaskDTO.
     * 
     * @param dto The DTO with updated values
     * @param bo The business object to update
     */
    void updateBOFromDTO(TaskDTO dto, @MappingTarget Task bo);
    
    /**
     * Update a TaskBO from a CreateTaskDTO.
     *
     * @param dto The DTO with updated values
     * @param bo The business object to update
     */
    void updateBOFromDTO(CreateTaskDTO dto, @MappingTarget Task bo);

    /**
     * Update a TaskBO from an UpdateTaskDTO.
     *
     * @param dto The DTO with updated values
     * @param bo The business object to update
     */
    void updateBOFromDTO(UpdateTaskDTO dto, @MappingTarget Task bo);
} 