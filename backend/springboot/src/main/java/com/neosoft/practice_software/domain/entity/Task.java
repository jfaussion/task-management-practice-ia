package com.neosoft.practice_software.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Task entity representing a task in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID assigneeId;

} 