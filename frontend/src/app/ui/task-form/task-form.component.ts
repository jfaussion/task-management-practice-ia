import { Component, Input, Output, EventEmitter, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../application/service/task.service';
import { UserService } from '../../application/service/user.service';
import { Task, TaskStatus, TaskPriority, CreateTaskRequest } from '../../domain/model/task.model';
import { User } from '../../domain/model/user.model';

@Component({
  selector: 'app-task-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
  standalone: true
})
export class TaskFormComponent implements OnInit {
  @Input() task: Task | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  private readonly taskService = inject(TaskService);
  private readonly userService = inject(UserService);

  users = signal<User[]>([]);
  isLoading = signal(false);

  title = '';
  description = '';
  status: TaskStatus = 'TODO';
  priority: TaskPriority = 'MEDIUM';
  dueDate = '';
  assigneeId = '';

  statuses: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];
  priorities: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH'];

  ngOnInit(): void {
    this.loadUsers();
    if (this.task) {
      this.title = this.task.title;
      this.description = this.task.description || '';
      this.status = this.task.status;
      this.priority = this.task.priority || 'MEDIUM';
      this.dueDate = this.task.dueDate ? this.task.dueDate.split('T')[0] : '';
      this.assigneeId = this.task.assigneeId || '';
    }
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (users) => this.users.set(users),
      error: (err) => console.error('Error loading users:', err)
    });
  }

  onSubmit(): void {
    if (!this.title.trim()) return;

    this.isLoading.set(true);

    const taskData: CreateTaskRequest = {
      title: this.title,
      description: this.description || undefined,
      status: this.status,
      priority: this.priority,
      dueDate: this.dueDate ? new Date(this.dueDate).toISOString() : undefined
    };

    const request = this.task?.id
      ? this.taskService.updateTask(this.task.id, taskData)
      : this.taskService.createTask(taskData);

    request.subscribe({
      next: () => {
        this.isLoading.set(false);
        this.saved.emit();
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Error saving task:', err);
      }
    });
  }

  onClose(): void {
    this.close.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.onClose();
    }
  }
}