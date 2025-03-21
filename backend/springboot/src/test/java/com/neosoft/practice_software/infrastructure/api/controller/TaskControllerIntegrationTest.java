package com.neosoft.practice_software.infrastructure.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String fileToString(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        String jsonContent = new String(Files.readAllBytes(resource.getFile().toPath()));
        return jsonContent;
    }

    @Test
    void getAllTasks_ShouldReturnTasksList() throws Exception {
        // Perform the GET request
        mockMvc.perform(get("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(fileToString("tasks/task-test-data.json"), true));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        // Get a valid task ID from test data
        UUID taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");

        // Get the specific task
        mockMvc.perform(get("/api/v1/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(fileToString("tasks/task-get-by-id.json"), true));
    }

    @Test
    void getTaskById_WithInvalidId_ShouldReturn404() throws Exception {
        UUID invalidId = UUID.randomUUID();
        
        mockMvc.perform(get("/api/v1/tasks/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
} 