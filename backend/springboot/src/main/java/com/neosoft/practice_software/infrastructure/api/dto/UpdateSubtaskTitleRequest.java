package com.neosoft.practice_software.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request payload to rename a Subtask.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubtaskTitleRequest {

    @NotBlank(message = "titre requis (non vide)")
    @Size(max = 200, message = "titre limité à 200 caractères")
    private String title;
}
