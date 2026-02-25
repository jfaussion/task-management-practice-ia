import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task, TaskStatus, TaskPriority } from '../../domain/model/task.model';

@Component({
  selector: 'app-task-card',
  imports: [CommonModule],
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
}