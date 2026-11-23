import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  inject,
  signal,
  computed
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubtaskService } from '../../application/service/subtask.service';
import { Task, TaskStatus, TaskPriority } from '../../domain/model/task.model';
import { Subtask } from '../../domain/model/subtask.model';
import { SubtaskFormComponent } from '../subtask-form/subtask-form.component';

/**
 * Card of the kanban. Hosts the subtask thumbnails, the progress bar
 * (derived on the fly, never stored) and the collapse/expand button
 * (collapsed state is not persisted).
 */
@Component({
  selector: 'app-task-card',
  imports: [CommonModule, SubtaskFormComponent],
  templateUrl: './task-card.component.html',
  standalone: true
})
export class TaskCardComponent implements OnChanges {
  @Input({ required: true }) task!: Task;
  @Output() edit = new EventEmitter<Task>();
  @Output() delete = new EventEmitter<void>();
  @Output() statusChange = new EventEmitter<{ task: Task; status: TaskStatus }>();

  private readonly subtaskService = inject(SubtaskService);

  subtasks = signal<Subtask[]>([]);
  collapsed = signal(false);
  showForm = signal(false);
  editingSubtask = signal<Subtask | null>(null);
  listError = signal<string | null>(null);

  priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH'];
  statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];

  progressPercent = computed(() => {
    const total = this.subtasks().length;
    if (total === 0) {
      return null;
    }
    const done = this.subtasks().filter((subtask) => subtask.done).length;
    return Math.round((done * 100) / total);
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['task'] && this.task) {
      this.subtasks.set([...(this.task.subtasks ?? [])]);
    }
  }

  getPriorityColor(priority?: TaskPriority): string {
    const colors: Record<TaskPriority, string> = {
      LOW: 'bg-gray-200 text-gray-700',
      MEDIUM: 'bg-yellow-200 text-yellow-800',
      HIGH: 'bg-red-200 text-red-800'
    };
    return priority ? colors[priority] : 'bg-gray-200 text-gray-700';
  }

  onEdit(): void {
    this.edit.emit(this.task);
  }

  onDelete(): void {
    this.delete.emit();
  }

  onStatusChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const newStatus = select.value as TaskStatus;
    if (newStatus !== this.task.status) {
      this.statusChange.emit({ task: this.task, status: newStatus });
    }
  }

  onAddSubtask(): void {
    this.editingSubtask.set(null);
    this.showForm.set(true);
  }

  onEditSubtask(subtask: Subtask): void {
    this.editingSubtask.set(subtask);
    this.showForm.set(true);
  }

  onSubtaskSaved(saved: Subtask): void {
    const editing = this.editingSubtask();
    if (editing) {
      this.subtasks.update((list) => list.map((s) => (s.id === saved.id ? saved : s)));
    } else {
      this.subtasks.update((list) => [...list, saved]);
    }
    // Adding a subtask auto-expands the list.
    this.collapsed.set(false);
    this.showForm.set(false);
    this.editingSubtask.set(null);
    this.listError.set(null);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.editingSubtask.set(null);
  }

  onToggleDone(subtask: Subtask, event: Event): void {
    const input = event.target as HTMLInputElement;
    this.listError.set(null);
    this.subtaskService.setSubtaskDone(this.task.id!, subtask.id!, input.checked).subscribe({
      next: (updated) =>
        this.subtasks.update((list) => list.map((s) => (s.id === updated.id ? updated : s))),
      error: () => {
        // No optimistic update: restore the checkbox and show a message.
        input.checked = subtask.done;
        this.listError.set("Une erreur est survenue. La sous-tâche n'a pas été mise à jour.");
      }
    });
  }

  onDeleteSubtask(subtask: Subtask): void {
    if (!window.confirm(`Supprimer la sous-tâche « ${subtask.title} » ?`)) {
      return;
    }
    this.listError.set(null);
    this.subtaskService.deleteSubtask(this.task.id!, subtask.id!).subscribe({
      next: () => this.subtasks.update((list) => list.filter((s) => s.id !== subtask.id)),
      error: () => this.listError.set("Une erreur est survenue. La sous-tâche n'a pas été supprimée.")
    });
  }

  toggleCollapsed(): void {
    this.collapsed.update((value) => !value);
  }
}
