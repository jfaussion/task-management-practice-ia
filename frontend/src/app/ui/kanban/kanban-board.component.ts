import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../application/service/task.service';
import { Task, TaskStatus } from '../../domain/model/task.model';
import { TaskCardComponent } from '../task-card/task-card.component';
import { TaskFormComponent } from '../task-form/task-form.component';

@Component({
  selector: 'app-kanban-board',
  imports: [CommonModule, TaskCardComponent, TaskFormComponent],
  templateUrl: './kanban-board.component.html',
  standalone: true
})
export class KanbanBoardComponent implements OnInit {
  private readonly taskService = inject(TaskService);

  tasks = signal<Task[]>([]);
  showForm = signal(false);
  editingTask = signal<Task | null>(null);

  columns: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];

  tasksByStatus = computed(() => {
    const currentTasks = this.tasks();
    return {
      TODO: currentTasks.filter(t => t.status === 'TODO'),
      IN_PROGRESS: currentTasks.filter(t => t.status === 'IN_PROGRESS'),
      DONE: currentTasks.filter(t => t.status === 'DONE')
    };
  });

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getAllTasks().subscribe({
      next: (tasks) => this.tasks.set(tasks),
      error: (err) => console.error('Error loading tasks:', err)
    });
  }

  openCreateForm(): void {
    this.editingTask.set(null);
    this.showForm.set(true);
  }

  openEditForm(task: Task): void {
    this.editingTask.set(task);
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.editingTask.set(null);
  }

  onTaskSaved(): void {
    this.closeForm();
    this.loadTasks();
  }

  onTaskDeleted(): void {
    this.loadTasks();
  }

  onStatusChange(task: Task, newStatus: TaskStatus): void {
    if (!task.id) return;
    this.taskService.updateTaskStatus(task.id, newStatus).subscribe({
      next: () => this.loadTasks(),
      error: (err) => console.error('Error updating status:', err)
    });
  }

  getColumnTitle(status: TaskStatus): string {
    const titles: Record<TaskStatus, string> = {
      'TODO': 'To Do',
      'IN_PROGRESS': 'In Progress',
      'DONE': 'Done'
    };
    return titles[status];
  }

  getColumnColor(status: TaskStatus): string {
    const colors: Record<TaskStatus, string> = {
      'TODO': 'bg-gray-100',
      'IN_PROGRESS': 'bg-blue-100',
      'DONE': 'bg-green-100'
    };
    return colors[status];
  }
}