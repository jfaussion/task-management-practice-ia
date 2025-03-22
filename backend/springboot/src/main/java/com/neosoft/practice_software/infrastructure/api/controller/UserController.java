package com.neosoft.practice_software.infrastructure.api.controller;

import com.neosoft.practice_software.application.service.UserService;
import com.neosoft.practice_software.domain.model.User;
import com.neosoft.practice_software.infrastructure.api.dto.CreateUserDTO;
import com.neosoft.practice_software.infrastructure.api.dto.UserDTO;
import com.neosoft.practice_software.infrastructure.api.mapper.UserDTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final UserService userService;
    private final UserDTOMapper userDTOMapper;
    
    public UserController(UserService userService, UserDTOMapper userDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }
    
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(userDTOMapper.toDTOs(users));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        Optional<User> userOpt = userService.getUserById(id);
        return userOpt.map(user -> ResponseEntity.ok(userDTOMapper.toDTO(user)))
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO createUserDTO) {
        User userBO = userDTOMapper.toBO(createUserDTO);
        User createdUser = userService.createUser(userBO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTOMapper.toDTO(createdUser));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        User userBO = userDTOMapper.toBO(userDTO);
        User updatedUser = userService.updateUser(id, userBO);
        return ResponseEntity.ok(userDTOMapper.toDTO(updatedUser));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
} 