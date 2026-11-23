import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subtask, CreateSubtaskRequest } from '../../domain/model/subtask.model';

/**
 * Service for subtask operations of a task, through the dev proxy.
 * Stateless: on server error the caller keeps its previous state
 * (no optimistic update).
 */
@Injectable({
  providedIn: 'root'
})
export class SubtaskService {
  private readonly http = inject(HttpClient);

  private url(taskId: string): string {
    return `/api/v1/tasks/${taskId}/subtasks`;
  }

  getSubtasks(taskId: string): Observable<Subtask[]> {
    return this.http.get<Subtask[]>(this.url(taskId));
  }

  createSubtask(taskId: string, title: string): Observable<Subtask> {
    const body: CreateSubtaskRequest = { title };
    return this.http.post<Subtask>(this.url(taskId), body);
  }

  renameSubtask(taskId: string, subtaskId: string, title: string): Observable<Subtask> {
    return this.http.put<Subtask>(`${this.url(taskId)}/${subtaskId}`, { title });
  }

  setSubtaskDone(taskId: string, subtaskId: string, done: boolean): Observable<Subtask> {
    return this.http.patch<Subtask>(`${this.url(taskId)}/${subtaskId}/done`, { done });
  }

  deleteSubtask(taskId: string, subtaskId: string): Observable<void> {
    return this.http.delete<void>(`${this.url(taskId)}/${subtaskId}`);
  }
}
