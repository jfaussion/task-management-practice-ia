import { Task } from '../../domain/model/task.model';

export const TASK_SERVICE = 'TASK_SERVICE';

export interface ITaskService {
  findAll(filters?: {
    status?: string;
    priority?: string;
    assigneeId?: string;
  }): Promise<Task[]>;
  findById(id: string): Promise<Task | null>;
  create(task: Partial<Task>): Promise<Task>;
  update(id: string, task: Partial<Task>): Promise<Task>;
  findByUserId(userId: string): Promise<Task[]>;
} 