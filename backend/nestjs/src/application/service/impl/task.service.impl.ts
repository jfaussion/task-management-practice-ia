import { Injectable, Inject } from '@nestjs/common';
import { ITaskService } from '../task.service';
import { ITaskDao, TASK_DAO } from '../../dao/task.dao';
import { Task } from '../../../domain/model/task.model';

@Injectable()
export class TaskServiceImpl implements ITaskService {
  constructor(
    @Inject(TASK_DAO)
    private readonly taskDao: ITaskDao,
  ) {}

  async findAll(filters?: {
    status?: string;
    priority?: string;
    assigneeId?: string;
  }): Promise<Task[]> {
    return this.taskDao.findAll(filters);
  }

  async findById(id: string): Promise<Task | null> {
    return this.taskDao.findById(id);
  }

  async create(task: Partial<Task>): Promise<Task> {
    return this.taskDao.create(task);
  }

  async update(id: string, task: Partial<Task>): Promise<Task> {
    return this.taskDao.update(id, task);
  }

  async findByUserId(userId: string): Promise<Task[]> {
    return this.taskDao.findByUserId(userId);
  }
} 