package com.neosoft.practice_software.infrastructure.api.controller;

import com.neosoft.practice_software.domain.exception.FunctionalException;
import com.neosoft.practice_software.domain.exception.SubtaskLimitExceededException;
import com.neosoft.practice_software.domain.exception.SubtaskNotFoundException;
import com.neosoft.practice_software.domain.exception.SubtaskValidationException;
import com.neosoft.practice_software.domain.exception.TechnicalException;
import com.neosoft.practice_software.infrastructure.api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FunctionalException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleFunctionalException(FunctionalException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.BAD_REQUEST.value(),
            "Functional Error",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SubtaskNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleSubtaskNotFoundException(SubtaskNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SubtaskLimitExceededException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleSubtaskLimitExceededException(SubtaskLimitExceededException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SubtaskValidationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleSubtaskValidationException(SubtaskValidationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Invalid request body",
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            "Ressource introuvable",
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TechnicalException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleTechnicalException(TechnicalException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Technical Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 