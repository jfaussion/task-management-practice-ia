import { Task } from '../../domain/model/task.model';

export const TASK_DAO = 'TASK_DAO';

export interface ITaskDao {
  findAll(filters?: {
    status?: string;
    priority?: string;
    assigneeId?: string;
  }): Promise<Task[]>;
  findById(id: string): Promise<Task | null>;
  create(task: Partial<Task>): Promise<Task>;
  update(id: string, task: Partial<Task>): Promise<Task>;
  delete(id: string): Promise<void>;
  findByUserId(userId: string): Promise<Task[]>;
} 