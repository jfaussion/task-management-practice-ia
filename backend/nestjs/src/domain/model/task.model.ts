export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE',
}

export class Task {
  id: string;
  title: string;
  description?: string;
  status: TaskStatus;
  priority?: string;
  dueDate?: Date;
  assigneeId?: string;
  createdAt: Date;
  updatedAt: Date;
} 