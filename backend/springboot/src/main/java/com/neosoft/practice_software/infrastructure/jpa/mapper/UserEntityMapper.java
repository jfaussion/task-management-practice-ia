package com.neosoft.practice_software.infrastructure.jpa.mapper;

import com.neosoft.practice_software.domain.model.User;
import com.neosoft.practice_software.infrastructure.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper for converting between UserEntity and User (Business Object).
 */
@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    
    /**
     * Convert UserEntity to User.
     * 
     * @param entity The entity to convert
     * @return The converted business object
     */
    User toBO(UserEntity entity);
    
    /**
     * Convert User to UserEntity.
     * 
     * @param bo The business object to convert
     * @return The converted entity
     */
    UserEntity toEntity(User bo);
    
    /**
     * Convert a list of UserEntity to a list of User.
     * 
     * @param entities The entities to convert
     * @return The converted business objects
     */
    List<User> toBOs(List<UserEntity> entities);
    
    /**
     * Update a UserEntity from a User.
     * 
     * @param bo The business object with updated values
     * @param entity The entity to update
     */
    void updateEntityFromBO(User bo, @MappingTarget UserEntity entity);
} 