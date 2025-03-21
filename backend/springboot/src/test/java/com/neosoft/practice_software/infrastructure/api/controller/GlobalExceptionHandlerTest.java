package com.neosoft.practice_software.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neosoft.practice_software.application.service.TaskService;
import com.neosoft.practice_software.domain.exception.FunctionalException;
import com.neosoft.practice_software.domain.exception.TechnicalException;
import com.neosoft.practice_software.infrastructure.api.dto.CreateTaskDTO;
import com.neosoft.practice_software.infrastructure.api.mapper.TaskDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskDTOMapper taskDTOMapper;

    @InjectMocks
    private TaskController taskController;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String FUNCTIONAL_ERROR_MESSAGE = "Task cannot be created with invalid status";
    private static final String TECHNICAL_ERROR_MESSAGE = "Database connection failed";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void whenServiceThrowsFunctionalException_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setTitle("Test Task");
        createTaskDTO.setDescription("Test Description");
        createTaskDTO.setStatus("TODO");
        createTaskDTO.setPriority("HIGH");

        when(taskDTOMapper.toBO(any(CreateTaskDTO.class)))
                .thenThrow(new FunctionalException(FUNCTIONAL_ERROR_MESSAGE));

        // When/Then
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Functional Error"))
                .andExpect(jsonPath("$.message").value(FUNCTIONAL_ERROR_MESSAGE))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void whenServiceThrowsTechnicalException_ShouldReturnInternalServerError() throws Exception {
        // Given
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setTitle("Test Task");
        createTaskDTO.setDescription("Test Description");
        createTaskDTO.setStatus("TODO");
        createTaskDTO.setPriority("HIGH");

        when(taskDTOMapper.toBO(any(CreateTaskDTO.class)))
                .thenThrow(new TechnicalException(TECHNICAL_ERROR_MESSAGE));

        // When/Then
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Technical Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void whenServiceThrowsIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateTaskDTO createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setTitle("Test Task");
        createTaskDTO.setDescription("Test Description");
        createTaskDTO.setStatus("INVALID_STATUS");
        createTaskDTO.setPriority("HIGH");

        when(taskDTOMapper.toBO(any(CreateTaskDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid status: INVALID_STATUS"));

        // When/Then
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Invalid status: INVALID_STATUS"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
} 