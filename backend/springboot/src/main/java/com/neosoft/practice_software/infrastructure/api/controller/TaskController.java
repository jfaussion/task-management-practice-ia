package com.neosoft.practice_software.infrastructure.api.controller;

import com.neosoft.practice_software.application.service.TaskService;
import com.neosoft.practice_software.domain.model.TaskBO;
import com.neosoft.practice_software.infrastructure.api.dto.CreateTaskDTO;
import com.neosoft.practice_software.infrastructure.api.dto.TaskDTO;
import com.neosoft.practice_software.infrastructure.api.mapper.TaskDTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing tasks.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    
    private final TaskService taskService;
    private final TaskDTOMapper taskDTOMapper;
    
    public TaskController(TaskService taskService, TaskDTOMapper taskDTOMapper) {
        this.taskService = taskService;
        this.taskDTOMapper = taskDTOMapper;
    }
    
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskBO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(taskDTOMapper.toDTOs(tasks));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable UUID id) {
        Optional<TaskBO> taskOpt = taskService.getTaskById(id);
        return taskOpt.map(task -> ResponseEntity.ok(taskDTOMapper.toDTO(task)))
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskDTO createTaskDTO) {
        TaskBO taskBO = taskDTOMapper.toBO(createTaskDTO);
        TaskBO createdTask = taskService.createTask(taskBO);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDTOMapper.toDTO(createdTask));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID id, @RequestBody TaskDTO taskDTO) {
        TaskBO taskBO = taskDTOMapper.toBO(taskDTO);
        TaskBO updatedTask = taskService.updateTask(id, taskBO);
        return ResponseEntity.ok(taskDTOMapper.toDTO(updatedTask));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        boolean deleted = taskService.deleteTask(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    @PutMapping("/{id}/assign")
    public ResponseEntity<TaskDTO> assignTask(@PathVariable UUID id, @RequestParam UUID assigneeId) {
        TaskBO updatedTask = taskService.assignTask(id, assigneeId);
        return ResponseEntity.ok(taskDTOMapper.toDTO(updatedTask));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable UUID id, @RequestParam String status) {
        TaskBO updatedTask = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(taskDTOMapper.toDTO(updatedTask));
    }
    
    @GetMapping("/{id}/estimate")
    public ResponseEntity<Double> estimateTaskTime(@PathVariable UUID id) {
        double estimatedTime = taskService.estimateTaskTime(id);
        return ResponseEntity.ok(estimatedTime);
    }
} 