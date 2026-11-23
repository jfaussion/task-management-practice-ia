export interface Subtask {
  id?: string;
  taskId?: string;
  title: string;
  done: boolean;
  createdAt?: string;
}

export interface CreateSubtaskRequest {
  title: string;
}
