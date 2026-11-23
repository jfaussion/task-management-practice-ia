package com.neosoft.practice_software.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Business Object for Subtask.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subtask {
    private UUID id;
    private UUID taskId;
    private String title;
    private boolean done;
    private LocalDateTime createdAt;
}
