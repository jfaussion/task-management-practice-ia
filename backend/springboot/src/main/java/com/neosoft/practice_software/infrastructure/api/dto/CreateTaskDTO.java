package com.neosoft.practice_software.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


/**
 * DTO for creating a new Task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskDTO {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(TODO|IN_PROGRESS|DONE)$", message = "Status must be one of: TODO, IN_PROGRESS, DONE")
    private String status;
    
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "Priority must be one of: LOW, MEDIUM, HIGH")
    private String priority;
    
    private LocalDate dueDate;
    private UUID assigneeId;
} 