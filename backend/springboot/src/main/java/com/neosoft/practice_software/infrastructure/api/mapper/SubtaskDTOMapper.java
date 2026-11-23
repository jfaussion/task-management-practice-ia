package com.neosoft.practice_software.infrastructure.api.mapper;

import com.neosoft.practice_software.domain.model.Subtask;
import com.neosoft.practice_software.infrastructure.api.dto.CreateSubtaskRequest;
import com.neosoft.practice_software.infrastructure.api.dto.SubtaskDTO;
import com.neosoft.practice_software.infrastructure.api.dto.UpdateSubtaskTitleRequest;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for converting between SubtaskDTO and Subtask (Business Object).
 */
@Mapper(componentModel = "spring")
public interface SubtaskDTOMapper {

    /**
     * Convert SubtaskBO to SubtaskDTO.
     *
     * @param bo The business object to convert
     * @return The converted DTO
     */
    SubtaskDTO toDTO(Subtask bo);

    /**
     * Convert a list of SubtaskBO to a list of SubtaskDTO.
     *
     * @param bos The business objects to convert
     * @return The converted DTOs
     */
    List<SubtaskDTO> toDTOs(List<Subtask> bos);

    /**
     * Convert CreateSubtaskRequest to SubtaskBO.
     *
     * @param dto The request to convert
     * @return The converted business object
     */
    Subtask toBO(CreateSubtaskRequest dto);

    /**
     * Convert UpdateSubtaskTitleRequest to SubtaskBO.
     *
     * @param dto The request to convert
     * @return The converted business object
     */
    Subtask toBO(UpdateSubtaskTitleRequest dto);
}
