package com.neosoft.practice_software.application.service;

import com.neosoft.practice_software.application.dao.SubtaskDAO;
import com.neosoft.practice_software.application.dao.TaskDAO;
import com.neosoft.practice_software.application.service.impl.SubtaskServiceImpl;
import com.neosoft.practice_software.domain.exception.SubtaskLimitExceededException;
import com.neosoft.practice_software.domain.exception.SubtaskNotFoundException;
import com.neosoft.practice_software.domain.exception.SubtaskValidationException;
import com.neosoft.practice_software.domain.model.Subtask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests of SubtaskService business rules (data-model.md):
 * title non-empty after trim and at most 200 characters, atomic limit of
 * 50 subtasks per task (Task.subtasks 0..50), done=false on creation,
 * creation order listing (created_at, id).
 */
@ExtendWith(MockitoExtension.class)
class SubtaskServiceTest {

    @Mock
    private SubtaskDAO subtaskDAO;

    @Mock
    private TaskDAO taskDAO;

    @InjectMocks
    private SubtaskServiceImpl subtaskService;

    private final UUID taskId = UUID.randomUUID();
    private final UUID subtaskId = UUID.randomUUID();

    private Subtask sampleSubtask() {
        Subtask subtask = new Subtask();
        subtask.setId(subtaskId);
        subtask.setTaskId(taskId);
        subtask.setTitle("Maquette");
        subtask.setDone(false);
        return subtask;
    }

    @Test
    void createSubtask_shouldCreateWithDefaultsAndTrimmedTitle() {
        when(taskDAO.existsById(taskId)).thenReturn(true);
        when(subtaskDAO.countByTaskId(taskId)).thenReturn(0L);
        when(subtaskDAO.save(any(Subtask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subtask created = subtaskService.createSubtask(taskId, "  Maquette  ");

        ArgumentCaptor<Subtask> captor = ArgumentCaptor.forClass(Subtask.class);
        verify(subtaskDAO).save(captor.capture());
        assertThat(created.getTitle()).isEqualTo("Maquette");
        assertThat(captor.getValue().getTitle()).isEqualTo("Maquette");
        assertThat(captor.getValue().getTaskId()).isEqualTo(taskId);
        assertThat(captor.getValue().isDone()).isFalse();
        assertThat(created.getId()).isNull();
    }

    @Test
    void createSubtask_shouldRejectBlankTitle() {
        assertThatThrownBy(() -> subtaskService.createSubtask(taskId, "   "))
                .isInstanceOf(SubtaskValidationException.class)
                .hasMessage("titre requis (non vide)");
        verify(subtaskDAO, never()).save(any(Subtask.class));
    }

    @Test
    void createSubtask_shouldRejectNullTitle() {
        assertThatThrownBy(() -> subtaskService.createSubtask(taskId, null))
                .isInstanceOf(SubtaskValidationException.class)
                .hasMessage("titre requis (non vide)");
    }

    @Test
    void createSubtask_shouldRejectTitleLongerThan200Characters() {
        String tooLong = "a".repeat(201);

        assertThatThrownBy(() -> subtaskService.createSubtask(taskId, tooLong))
                .isInstanceOf(SubtaskValidationException.class)
                .hasMessage("titre limité à 200 caractères");
        verify(subtaskDAO, never()).save(any(Subtask.class));
    }

    @Test
    void createSubtask_shouldRejectWhenParentTaskDoesNotExist() {
        when(taskDAO.existsById(taskId)).thenReturn(false);

        assertThatThrownBy(() -> subtaskService.createSubtask(taskId, "Maquette"))
                .isInstanceOf(SubtaskNotFoundException.class);
        verify(subtaskDAO, never()).save(any(Subtask.class));
    }

    @Test
    void createSubtask_shouldReject51stSubtask() {
        when(taskDAO.existsById(taskId)).thenReturn(true);
        when(subtaskDAO.countByTaskId(taskId)).thenReturn(50L);

        assertThatThrownBy(() -> subtaskService.createSubtask(taskId, "51e"))
                .isInstanceOf(SubtaskLimitExceededException.class)
                .hasMessage("limite de 50 sous-tâches par tâche atteinte");
        verify(subtaskDAO, never()).save(any(Subtask.class));
    }

    @Test
    void createSubtask_shouldAccept50thSubtask() {
        when(taskDAO.existsById(taskId)).thenReturn(true);
        when(subtaskDAO.countByTaskId(taskId)).thenReturn(49L);
        when(subtaskDAO.save(any(Subtask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subtask created = subtaskService.createSubtask(taskId, "50e");

        assertThat(created.getTitle()).isEqualTo("50e");
    }

    @Test
    void renameSubtask_shouldUpdateTitle() {
        Subtask existing = sampleSubtask();
        when(subtaskDAO.findByIdAndTaskId(subtaskId, taskId)).thenReturn(Optional.of(existing));
        when(subtaskDAO.update(any(Subtask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subtask renamed = subtaskService.renameSubtask(taskId, subtaskId, "  Nouveau titre  ");

        assertThat(renamed.getTitle()).isEqualTo("Nouveau titre");
        assertThat(renamed.getTaskId()).isEqualTo(taskId);
    }

    @Test
    void renameSubtask_shouldRejectBlankTitle() {
        assertThatThrownBy(() -> subtaskService.renameSubtask(taskId, subtaskId, " "))
                .isInstanceOf(SubtaskValidationException.class)
                .hasMessage("titre requis (non vide)");
        verify(subtaskDAO, never()).update(any(Subtask.class));
    }

    @Test
    void renameSubtask_shouldRejectTitleLongerThan200Characters() {
        assertThatThrownBy(() -> subtaskService.renameSubtask(taskId, subtaskId, "a".repeat(201)))
                .isInstanceOf(SubtaskValidationException.class)
                .hasMessage("titre limité à 200 caractères");
        verify(subtaskDAO, never()).update(any(Subtask.class));
    }

    @Test
    void renameSubtask_shouldFailWhenSubtaskDoesNotExist() {
        when(subtaskDAO.findByIdAndTaskId(subtaskId, taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subtaskService.renameSubtask(taskId, subtaskId, "Nouveau titre"))
                .isInstanceOf(SubtaskNotFoundException.class);
    }

    @Test
    void setSubtaskDone_shouldSetDoneToTrue() {
        Subtask existing = sampleSubtask();
        when(subtaskDAO.findByIdAndTaskId(subtaskId, taskId)).thenReturn(Optional.of(existing));
        when(subtaskDAO.update(any(Subtask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subtask updated = subtaskService.setSubtaskDone(taskId, subtaskId, true);

        assertThat(updated.isDone()).isTrue();
    }

    @Test
    void setSubtaskDone_shouldSetDoneBackToFalse() {
        Subtask existing = sampleSubtask();
        existing.setDone(true);
        when(subtaskDAO.findByIdAndTaskId(subtaskId, taskId)).thenReturn(Optional.of(existing));
        when(subtaskDAO.update(any(Subtask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subtask updated = subtaskService.setSubtaskDone(taskId, subtaskId, false);

        assertThat(updated.isDone()).isFalse();
    }

    @Test
    void setSubtaskDone_shouldFailWhenSubtaskDoesNotExist() {
        when(subtaskDAO.findByIdAndTaskId(subtaskId, taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subtaskService.setSubtaskDone(taskId, subtaskId, true))
                .isInstanceOf(SubtaskNotFoundException.class);
    }

    @Test
    void deleteSubtask_shouldDeleteWithoutTouchingTheTask() {
        Subtask existing = sampleSubtask();
        when(subtaskDAO.findByIdAndTaskId(subtaskId, taskId)).thenReturn(Optional.of(existing));

        subtaskService.deleteSubtask(taskId, subtaskId);

        verify(subtaskDAO).deleteById(subtaskId);
        verify(taskDAO, never()).existsById(any(UUID.class));
    }

    @Test
    void deleteSubtask_shouldFailWhenSubtaskDoesNotExist() {
        when(subtaskDAO.findByIdAndTaskId(subtaskId, taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subtaskService.deleteSubtask(taskId, subtaskId))
                .isInstanceOf(SubtaskNotFoundException.class);
        verify(subtaskDAO, never()).deleteById(any(UUID.class));
    }

    @Test
    void getSubtasksByTaskId_shouldReturnSubtasksInCreationOrder() {
        when(taskDAO.existsById(taskId)).thenReturn(true);
        Subtask first = sampleSubtask();
        Subtask second = sampleSubtask();
        when(subtaskDAO.findByTaskId(taskId)).thenReturn(List.of(first, second));

        List<Subtask> subtasks = subtaskService.getSubtasksByTaskId(taskId);

        assertThat(subtasks).containsExactly(first, second);
    }

    @Test
    void getSubtasksByTaskId_shouldFailWhenTaskDoesNotExist() {
        when(taskDAO.existsById(taskId)).thenReturn(false);

        assertThatThrownBy(() -> subtaskService.getSubtasksByTaskId(taskId))
                .isInstanceOf(SubtaskNotFoundException.class);
    }
}
