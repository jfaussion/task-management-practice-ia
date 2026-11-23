package com.neosoft.practice_software.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object for Subtask.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskDTO {

    private UUID id;

    private UUID taskId;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    private boolean done;

    private LocalDateTime createdAt;
}
