package com.neosoft.practice_software.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object for Task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    
    private UUID id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(TODO|IN_PROGRESS|DONE)$", message = "Status must be one of: TODO, IN_PROGRESS, DONE")
    private String status;
    
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "Priority must be one of: LOW, MEDIUM, HIGH")
    private String priority;
    
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID assigneeId;
    private UserDTO assignee;
} 