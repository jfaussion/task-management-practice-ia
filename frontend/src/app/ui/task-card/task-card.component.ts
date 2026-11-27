import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideChevronDown, LucidePencil, LucideTrash2 } from '@lucide/angular';
import { Task, TaskStatus, TaskPriority } from '../../domain/model/task.model';
import { IconButtonComponent } from '../neo/icon-button.component';

@Component({
  selector: 'app-task-card',
  imports: [CommonModule, IconButtonComponent, LucidePencil, LucideTrash2, LucideChevronDown],
  templateUrl: './task-card.component.html',
  standalone: true
})
export class TaskCardComponent {
  @Input({ required: true }) task!: Task;
  @Output() edit = new EventEmitter<Task>();
  @Output() delete = new EventEmitter<void>();
  @Output() statusChange = new EventEmitter<{ task: Task; status: TaskStatus }>();

  priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH'];
  statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];

  getPriorityColor(priority?: TaskPriority): string {
    // Échelle d'intensité de la charte : une seule teinte violette.
    const colors: Record<TaskPriority, string> = {
      LOW: 'bg-neo-purple/7 text-neo-blue-dark/70',
      MEDIUM: 'bg-neo-purple/22 text-neo-purple-deep',
      HIGH: 'bg-neo-purple text-white'
    };
    return priority ? colors[priority] : 'bg-neo-purple/7 text-neo-blue-dark/70';
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
}