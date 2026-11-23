package com.neosoft.practice_software.domain.exception;

/**
 * Thrown when the limit of 50 subtasks per task is reached (HTTP 409).
 */
public class SubtaskLimitExceededException extends RuntimeException {
    public SubtaskLimitExceededException(String message) {
        super(message);
    }
}
