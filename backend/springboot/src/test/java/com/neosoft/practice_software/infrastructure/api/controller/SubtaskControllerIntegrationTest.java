package com.neosoft.practice_software.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neosoft.practice_software.infrastructure.jpa.repository.JpaSubtaskRepository;
import com.neosoft.practice_software.infrastructure.jpa.repository.JpaTaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests of the subtask REST API against the seeded H2 database
 * (contracts/subtasks-api.md). Each test creates its own task so tests stay
 * isolated by construction.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SubtaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaTaskRepository taskRepository;

    @Autowired
    private JpaSubtaskRepository subtaskRepository;

    private String createTask(String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", title,
                                "status", "TODO"))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private String createSubtask(String taskId, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", title))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void createSubtask_shouldReturn201WithDoneFalseByDefault() throws Exception {
        String taskId = createTask("Task with subtasks");

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Rédiger la maquette\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Rédiger la maquette"))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getSubtasks_shouldReturnCreationOrder() throws Exception {
        String taskId = createTask("Ordered subtasks");
        createSubtask(taskId, "First");
        createSubtask(taskId, "Second");

        mockMvc.perform(get("/api/v1/tasks/{taskId}/subtasks", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("First"))
                .andExpect(jsonPath("$[1].title").value("Second"));
    }

    @Test
    void getSubtasks_shouldReturnEmptyListForTaskWithoutSubtasks() throws Exception {
        String taskId = createTask("No subtasks");

        mockMvc.perform(get("/api/v1/tasks/{taskId}/subtasks", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void renameSubtask_shouldReturn200WithUpdatedTitle() throws Exception {
        String taskId = createTask("Task to rename subtask");
        String subtaskId = createSubtask(taskId, "Old title");

        mockMvc.perform(put("/api/v1/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nouveau titre\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subtaskId))
                .andExpect(jsonPath("$.title").value("Nouveau titre"));
    }

    @Test
    void deleteSubtask_shouldReturn204AndLeaveTaskIntact() throws Exception {
        String taskId = createTask("Task to delete subtask");
        createSubtask(taskId, "Doomed");

        mockMvc.perform(delete("/api/v1/tasks/{taskId}/subtasks/{subtaskId}", taskId,
                        subtaskRepository.findByTaskIdOrderByCreatedAtAscIdAsc(UUID.fromString(taskId)).get(0).getId().toString()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/tasks/{taskId}/subtasks", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        assertThat(taskRepository.existsById(UUID.fromString(taskId))).isTrue();
    }

    @Test
    void createSubtask_shouldReturn400WhenTitleIsBlank() throws Exception {
        String taskId = createTask("Blank title task");

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("titre requis (non vide)"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createSubtask_shouldReturn400WhenTitleIsLongerThan200Characters() throws Exception {
        String taskId = createTask("Long title task");

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "a".repeat(201)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("titre limité à 200 caractères"));
    }

    @Test
    void renameSubtask_shouldReturn400WhenTitleIsBlank() throws Exception {
        String taskId = createTask("Blank rename task");
        String subtaskId = createSubtask(taskId, "Current");

        mockMvc.perform(put("/api/v1/tasks/{taskId}/subtasks/{subtaskId}", taskId, subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("titre requis (non vide)"));
    }

    @Test
    void createSubtask_shouldReturn404WhenParentTaskDoesNotExist() throws Exception {
        UUID unknownTaskId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", unknownTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Orphan\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getSubtasks_shouldReturn404WhenTaskDoesNotExist() throws Exception {
        UUID unknownTaskId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/tasks/{taskId}/subtasks", unknownTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void renameSubtask_shouldReturn404WhenSubtaskDoesNotExist() throws Exception {
        String taskId = createTask("Unknown subtask rename");
        UUID unknownSubtaskId = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/tasks/{taskId}/subtasks/{subtaskId}", taskId, unknownSubtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nouveau titre\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSubtask_shouldReturn404WhenSubtaskDoesNotExist() throws Exception {
        String taskId = createTask("Unknown subtask delete");
        UUID unknownSubtaskId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/tasks/{taskId}/subtasks/{subtaskId}", taskId, unknownSubtaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void setSubtaskDone_shouldReturn200AndToggleDone() throws Exception {
        String taskId = createTask("Task to toggle subtask");
        String subtaskId = createSubtask(taskId, "Toggle me");

        mockMvc.perform(patch("/api/v1/tasks/{taskId}/subtasks/{subtaskId}/done", taskId, subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subtaskId))
                .andExpect(jsonPath("$.done").value(true));

        mockMvc.perform(patch("/api/v1/tasks/{taskId}/subtasks/{subtaskId}/done", taskId, subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void setSubtaskDone_shouldReturn400WhenBodyIsNotBoolean() throws Exception {
        String taskId = createTask("Task invalid toggle");
        String subtaskId = createSubtask(taskId, "Toggle me");

        mockMvc.perform(patch("/api/v1/tasks/{taskId}/subtasks/{subtaskId}/done", taskId, subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":\"yes\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/v1/tasks/{taskId}/subtasks/{subtaskId}/done", taskId, subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void setSubtaskDone_shouldReturn404WhenSubtaskDoesNotExist() throws Exception {
        String taskId = createTask("Task unknown toggle");
        UUID unknownSubtaskId = UUID.randomUUID();

        mockMvc.perform(patch("/api/v1/tasks/{taskId}/subtasks/{subtaskId}/done", taskId, unknownSubtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"done\":true}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSubtask_shouldReturn409WhenLimitOf50IsReached() throws Exception {
        String taskId = createTask("Task at limit");
        for (int i = 1; i <= 50; i++) {
            createSubtask(taskId, "Subtask " + i);
        }

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Subtask 51\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("limite de 50 sous-tâches par tâche atteinte"))
                .andExpect(jsonPath("$.status").value(409));

        assertThat(subtaskRepository.countByTaskId(UUID.fromString(taskId))).isEqualTo(50);
    }

    @Test
    void getTaskById_shouldIncludeSubtasksInCreationOrder() throws Exception {
        String taskId = createTask("Task payload enrichment");
        createSubtask(taskId, "First");
        createSubtask(taskId, "Second");

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtasks.length()").value(2))
                .andExpect(jsonPath("$.subtasks[0].title").value("First"))
                .andExpect(jsonPath("$.subtasks[1].title").value("Second"));
    }

    @Test
    void getAllTasks_shouldIncludeSubtasks() throws Exception {
        String taskId = createTask("Task list enrichment");
        createSubtask(taskId, "Listed subtask");

        MvcResult result = mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        var tasks = objectMapper.readTree(result.getResponse().getContentAsString());
        var enriched = new java.util.ArrayList<com.fasterxml.jackson.databind.JsonNode>();
        tasks.forEach(task -> {
            if (task.get("id").asText().equals(taskId)) {
                enriched.add(task);
            }
        });

        assertThat(enriched).hasSize(1);
        assertThat(enriched.get(0).get("subtasks").get(0).get("title").asText()).isEqualTo("Listed subtask");
    }

    @Test
    void deletingTask_shouldCascadeSubtasks() throws Exception {
        String taskId = createTask("Task to cascade");
        createSubtask(taskId, "Subtask A");
        createSubtask(taskId, "Subtask B");

        taskRepository.deleteById(UUID.fromString(taskId));

        assertThat(subtaskRepository.countByTaskId(UUID.fromString(taskId))).isZero();
    }

    @Test
    void attachingSubtaskToSubtask_shouldNotBePossible() throws Exception {
        // FR-002 / SC-004: no route allows attaching a subtask to a subtask.
        String taskId = createTask("Task with subtask");
        String subtaskId = createSubtask(taskId, "Cannot have children");

        mockMvc.perform(post("/api/v1/tasks/{taskId}/subtasks/{subtaskId}/subtasks", taskId, subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Grandchild\"}"))
                .andExpect(status().isNotFound());
    }
}
