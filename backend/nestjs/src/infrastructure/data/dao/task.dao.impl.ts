import { Injectable } from '@nestjs/common';
import { ITaskDao } from '../../../application/dao/task.dao';
import { Task } from '../../../domain/model/task.model';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class TaskDaoImpl implements ITaskDao {
  constructor(private readonly prisma: PrismaService) {}

  async findAll(filters?: {
    status?: string;
    priority?: string;
    assigneeId?: string;
  }): Promise<Task[]> {
    const where: any = {};
    
    if (filters?.status) {
      where.status = filters.status;
    }
    
    if (filters?.priority) {
      where.priority = filters.priority;
    }
    
    if (filters?.assigneeId) {
      where.assigneeId = filters.assigneeId;
    }
    
    const tasks = await this.prisma.task.findMany({
      where,
    });
    
    return tasks.map(this.mapToTask);
  }

  async findById(id: string): Promise<Task | null> {
    const task = await this.prisma.task.findUnique({
      where: { id },
    });
    return task ? this.mapToTask(task) : null;
  }

  async create(task: Partial<Task>): Promise<Task> {
    const createdTask = await this.prisma.task.create({
      data: {
        title: task.title || '',
        description: task.description,
        status: task.status || '',
        priority: task.priority,
        dueDate: task.dueDate,
        assigneeId: task.assigneeId,
      },
    });
    return this.mapToTask(createdTask);
  }

  async update(id: string, task: Partial<Task>): Promise<Task> {
    const updatedTask = await this.prisma.task.update({
      where: { id },
      data: {
        title: task.title,
        description: task.description,
        status: task.status,
        priority: task.priority,
        dueDate: task.dueDate,
        assigneeId: task.assigneeId,
      },
    });
    return this.mapToTask(updatedTask);
  }

  async delete(id: string): Promise<void> {
    await this.prisma.task.delete({
      where: { id },
    });
  }

  async findByUserId(userId: string): Promise<Task[]> {
    const tasks = await this.prisma.task.findMany({
      where: { assigneeId: userId },
    });
    return tasks.map(this.mapToTask);
  }

  private mapToTask(prismaTask: any): Task {
    const task = new Task();
    task.id = prismaTask.id;
    task.title = prismaTask.title;
    task.description = prismaTask.description;
    task.status = prismaTask.status;
    task.priority = prismaTask.priority;
    task.dueDate = prismaTask.dueDate;
    task.assigneeId = prismaTask.assigneeId;
    task.createdAt = prismaTask.createdAt;
    task.updatedAt = prismaTask.updatedAt;
    return task;
  }
} 