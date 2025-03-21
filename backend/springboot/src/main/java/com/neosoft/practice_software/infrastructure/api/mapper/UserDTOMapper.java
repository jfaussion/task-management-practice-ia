package com.neosoft.practice_software.infrastructure.api.mapper;

import com.neosoft.practice_software.domain.model.UserBO;
import com.neosoft.practice_software.infrastructure.api.dto.CreateUserDTO;
import com.neosoft.practice_software.infrastructure.api.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper for converting between UserDTO and UserBO.
 */
@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    
    /**
     * Convert UserBO to UserDTO.
     * 
     * @param bo The business object to convert
     * @return The converted DTO
     */
    UserDTO toDTO(UserBO bo);
    
    /**
     * Convert UserDTO to UserBO.
     * 
     * @param dto The DTO to convert
     * @return The converted business object
     */
    UserBO toBO(UserDTO dto);
    
    /**
     * Convert CreateUserDTO to UserBO.
     * 
     * @param dto The DTO to convert
     * @return The converted business object
     */
    UserBO toBO(CreateUserDTO dto);
    
    /**
     * Convert a list of UserBO to a list of UserDTO.
     * 
     * @param bos The business objects to convert
     * @return The converted DTOs
     */
    List<UserDTO> toDTOs(List<UserBO> bos);
    
    /**
     * Update a UserBO from a UserDTO.
     * 
     * @param dto The DTO with updated values
     * @param bo The business object to update
     */
    void updateBOFromDTO(UserDTO dto, @MappingTarget UserBO bo);
    
    /**
     * Update a UserBO from a CreateUserDTO.
     * 
     * @param dto The DTO with updated values
     * @param bo The business object to update
     */
    void updateBOFromDTO(CreateUserDTO dto, @MappingTarget UserBO bo);
} 