package com.neosoft.practice_software.infrastructure.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request payload to check / uncheck a Subtask.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetSubtaskDoneRequest {

    @NotNull(message = "done est requis (booléen)")
    private Boolean done;
}
