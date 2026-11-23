package com.neosoft.practice_software.domain.exception;

/**
 * Thrown when a subtask payload is invalid, e.g. empty or too long title (HTTP 400).
 */
public class SubtaskValidationException extends RuntimeException {
    public SubtaskValidationException(String message) {
        super(message);
    }
}
