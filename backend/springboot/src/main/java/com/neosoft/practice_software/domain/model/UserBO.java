package com.neosoft.practice_software.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Business Object for User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBO {
    private UUID id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 