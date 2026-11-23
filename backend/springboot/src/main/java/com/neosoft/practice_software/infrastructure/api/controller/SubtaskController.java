package com.neosoft.practice_software.infrastructure.api.controller;

import com.neosoft.practice_software.application.service.SubtaskService;
import com.neosoft.practice_software.domain.exception.SubtaskValidationException;
import com.neosoft.practice_software.domain.model.Subtask;
import com.neosoft.practice_software.infrastructure.api.dto.CreateSubtaskRequest;
import com.neosoft.practice_software.infrastructure.api.dto.SetSubtaskDoneRequest;
import com.neosoft.practice_software.infrastructure.api.dto.SubtaskDTO;
import com.neosoft.practice_software.infrastructure.api.dto.UpdateSubtaskTitleRequest;
import com.neosoft.practice_software.infrastructure.api.mapper.SubtaskDTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing subtasks of a task.
 */
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/subtasks")
public class SubtaskController {

    private final SubtaskService subtaskService;
    private final SubtaskDTOMapper subtaskDTOMapper;

    public SubtaskController(SubtaskService subtaskService, SubtaskDTOMapper subtaskDTOMapper) {
        this.subtaskService = subtaskService;
        this.subtaskDTOMapper = subtaskDTOMapper;
    }

    @GetMapping
    public ResponseEntity<List<SubtaskDTO>> getSubtasks(@PathVariable UUID taskId) {
        List<Subtask> subtasks = subtaskService.getSubtasksByTaskId(taskId);
        return ResponseEntity.ok(subtaskDTOMapper.toDTOs(subtasks));
    }

    @PostMapping
    public ResponseEntity<SubtaskDTO> createSubtask(@PathVariable UUID taskId,
                                                    @RequestBody CreateSubtaskRequest createSubtaskRequest) {
        Subtask createdSubtask = subtaskService.createSubtask(taskId, createSubtaskRequest.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(subtaskDTOMapper.toDTO(createdSubtask));
    }

    @PutMapping("/{subtaskId}")
    public ResponseEntity<SubtaskDTO> renameSubtask(@PathVariable UUID taskId,
                                                    @PathVariable UUID subtaskId,
                                                    @RequestBody UpdateSubtaskTitleRequest updateSubtaskTitleRequest) {
        Subtask updatedSubtask = subtaskService.renameSubtask(taskId, subtaskId, updateSubtaskTitleRequest.getTitle());
        return ResponseEntity.ok(subtaskDTOMapper.toDTO(updatedSubtask));
    }

    @PatchMapping("/{subtaskId}/done")
    public ResponseEntity<SubtaskDTO> setSubtaskDone(@PathVariable UUID taskId,
                                                     @PathVariable UUID subtaskId,
                                                     @RequestBody SetSubtaskDoneRequest setSubtaskDoneRequest) {
        if (setSubtaskDoneRequest == null || setSubtaskDoneRequest.getDone() == null) {
            throw new SubtaskValidationException("done est requis (booléen)");
        }
        Subtask updatedSubtask = subtaskService.setSubtaskDone(taskId, subtaskId, setSubtaskDoneRequest.getDone());
        return ResponseEntity.ok(subtaskDTOMapper.toDTO(updatedSubtask));
    }

    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<Void> deleteSubtask(@PathVariable UUID taskId, @PathVariable UUID subtaskId) {
        subtaskService.deleteSubtask(taskId, subtaskId);
        return ResponseEntity.noContent().build();
    }
}
