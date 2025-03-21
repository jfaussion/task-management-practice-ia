package com.neosoft.practice_software.infrastructure.jpa.mapper;

import com.neosoft.practice_software.domain.model.UserBO;
import com.neosoft.practice_software.infrastructure.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper for converting between UserEntity and UserBO.
 */
@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    
    /**
     * Convert UserEntity to UserBO.
     * 
     * @param entity The entity to convert
     * @return The converted business object
     */
    UserBO toBO(UserEntity entity);
    
    /**
     * Convert UserBO to UserEntity.
     * 
     * @param bo The business object to convert
     * @return The converted entity
     */
    UserEntity toEntity(UserBO bo);
    
    /**
     * Convert a list of UserEntity to a list of UserBO.
     * 
     * @param entities The entities to convert
     * @return The converted business objects
     */
    List<UserBO> toBOs(List<UserEntity> entities);
    
    /**
     * Update a UserEntity from a UserBO.
     * 
     * @param bo The business object with updated values
     * @param entity The entity to update
     */
    void updateEntityFromBO(UserBO bo, @MappingTarget UserEntity entity);
} 