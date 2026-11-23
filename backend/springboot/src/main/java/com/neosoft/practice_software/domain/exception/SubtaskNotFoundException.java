package com.neosoft.practice_software.domain.exception;

/**
 * Thrown when a referenced task or subtask cannot be found (HTTP 404).
 */
public class SubtaskNotFoundException extends RuntimeException {
    public SubtaskNotFoundException(String message) {
        super(message);
    }

    public SubtaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
