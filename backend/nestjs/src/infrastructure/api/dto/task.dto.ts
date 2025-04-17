import { TaskStatus } from '../../../domain/model/task.model';

export class CreateTaskDto {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority?: string;
  dueDate?: Date;
  assigneeId?: string;
}

export class UpdateTaskDto {
  title?: string;
  description?: string;
  status?: TaskStatus;
  priority?: string;
  dueDate?: Date;
  assigneeId?: string;
} 