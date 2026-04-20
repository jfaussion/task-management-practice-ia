package com.neosoft.practice_software.infrastructure.api.controller;

import com.neosoft.practice_software.application.service.UserService;
import com.neosoft.practice_software.domain.model.User;
import com.neosoft.practice_software.infrastructure.api.dto.CreateUserDTO;
import com.neosoft.practice_software.infrastructure.api.dto.UserDTO;
import com.neosoft.practice_software.infrastructure.api.mapper.UserDTOMapper;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserDTOMapper userDTOMapper;

    public UserController(UserService userService, UserDTOMapper userDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }

    /**
     * Retrieve all users with optional username search and pagination support.
     *
     * @param username Optional username search term for case-insensitive partial
     *                 matching.
     *                 Must be between 2 and 50 characters if provided.
     * @param pageable Optional pagination and sorting parameters.
     *                 Default page size is 20, maximum is 100.
     * @return ResponseEntity containing either:
     *         <ul>
     *         <li>List&lt;UserDTO&gt; when no pagination parameters are
     *         provided</li>
     *         <li>Page&lt;UserDTO&gt; when pagination parameters are provided</li>
     *         </ul>
     *         Returns HTTP 200 (OK) for successful requests, even when no users are
     *         found.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) @Size(min = 2, max = 50, message = "Username search term must be between 2 and 50 characters") String username,
            @PageableDefault(size = 20, page = 0) Pageable pageable) {
        // Sanitize username for logging (remove potential log injection characters)
        String sanitizedUsername = username != null ? username.replaceAll("[\r\n\t]", "_") : null;
        logger.info("GET /api/v1/users - username: '{}', pageable: {}",
                sanitizedUsername, pageable);

        boolean isPaginationRequested = isPaginationRequested(pageable);

        if (StringUtils.hasText(username)) {
            // Validate username parameter
            String trimmedUsername = username.trim();
            if (trimmedUsername.length() < 2 || trimmedUsername.length() > 50) {
                logger.warn("Invalid username search term length: {}", trimmedUsername.length());
                return ResponseEntity.badRequest().build();
            }

            if (isPaginationRequested) {
                // Search with pagination
                Page<UserDTO> userDTOPage = userService.searchUsersByUsername(trimmedUsername, pageable)
                        .map(userDTOMapper::toDTO);
                return ResponseEntity.ok(userDTOPage);
            } else {
                // Search without pagination
                var userDTOs = userDTOMapper.toDTOs(userService.searchUsersByUsername(trimmedUsername));
                return ResponseEntity.ok(userDTOs);
            }
        } else {
            if (isPaginationRequested) {
                // Get all users with pagination
                var userDTOPage = userService.getAllUsers(pageable).map(userDTOMapper::toDTO);
                return ResponseEntity.ok(userDTOPage);
            } else {
                // Get all users without pagination
                var userDTOs = userDTOMapper.toDTOs(userService.getAllUsers());
                return ResponseEntity.ok(userDTOs);
            }
        }
    }

    private boolean isPaginationRequested(Pageable pageable) {
        // Check if page number is not 0 or size is not the default
        return pageable.getPageNumber() > 0 ||
                pageable.getPageSize() != 20 ||
                pageable.getSort().isSorted();
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