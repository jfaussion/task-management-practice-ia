import { Component, Input, Output, EventEmitter, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SubtaskService } from '../../application/service/subtask.service';
import { Subtask } from '../../domain/model/subtask.model';

/**
 * Small inline form to create or rename a subtask.
 * Mirror validation: title required (non-empty after trim) and at most
 * 200 characters, with clear messages. On server error (e.g. the parent
 * task disappeared) the form stays open with a generic error message.
 */
@Component({
  selector: 'app-subtask-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './subtask-form.component.html',
  standalone: true
})
export class SubtaskFormComponent implements OnInit {
  @Input({ required: true }) taskId!: string;
  @Input() subtask: Subtask | null = null;
  @Output() saved = new EventEmitter<Subtask>();
  @Output() cancelled = new EventEmitter<void>();

  private readonly subtaskService = inject(SubtaskService);

  title = '';
  error = signal<string | null>(null);
  isSubmitting = signal(false);

  ngOnInit(): void {
    if (this.subtask) {
      this.title = this.subtask.title;
    }
  }

  onSubmit(): void {
    const trimmed = this.title.trim();
    if (!trimmed) {
      this.error.set('Le titre est requis (non vide).');
      return;
    }
    if (trimmed.length > 200) {
      this.error.set('Le titre est limité à 200 caractères.');
      return;
    }

    this.isSubmitting.set(true);
    this.error.set(null);

    const request = this.subtask
      ? this.subtaskService.renameSubtask(this.taskId, this.subtask.id!, trimmed)
      : this.subtaskService.createSubtask(this.taskId, trimmed);

    request.subscribe({
      next: (subtask) => {
        this.isSubmitting.set(false);
        this.saved.emit(subtask);
      },
      error: () => {
        this.isSubmitting.set(false);
        this.error.set("Une erreur est survenue. La sous-tâche n'a pas été enregistrée.");
      }
    });
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
