import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SubtaskService } from './subtask.service';
import { Subtask } from '../../domain/model/subtask.model';

describe('SubtaskService', () => {
  let service: SubtaskService;
  let httpMock: HttpTestingController;

  const taskId = 'task-1';
  const subtaskId = 'sub-1';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(SubtaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should fetch subtasks of a task via the proxy', () => {
    let result: Subtask[] | undefined;

    service.getSubtasks(taskId).subscribe((subtasks) => (result = subtasks));

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks');
    expect(req.request.method).toBe('GET');
    req.flush([{ id: subtaskId, taskId, title: 'Maquette', done: false }]);

    expect(result).toBeDefined();
    expect(result!.length).toBe(1);
    expect(result![0].title).toBe('Maquette');
  });

  it('should create a subtask', () => {
    let result: Subtask | undefined;

    service.createSubtask(taskId, 'Nouvelle sous-tâche').subscribe((subtask) => (result = subtask));

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ title: 'Nouvelle sous-tâche' });
    req.flush({ id: subtaskId, taskId, title: 'Nouvelle sous-tâche', done: false });

    expect(result?.done).toBe(false);
  });

  it('should rename a subtask', () => {
    let result: Subtask | undefined;

    service.renameSubtask(taskId, subtaskId, 'Nouveau titre').subscribe((subtask) => (result = subtask));

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks/sub-1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ title: 'Nouveau titre' });
    req.flush({ id: subtaskId, taskId, title: 'Nouveau titre', done: false });

    expect(result?.title).toBe('Nouveau titre');
  });

  it('should check / uncheck a subtask', () => {
    let result: Subtask | undefined;

    service.setSubtaskDone(taskId, subtaskId, true).subscribe((subtask) => (result = subtask));

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks/sub-1/done');
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ done: true });
    req.flush({ id: subtaskId, taskId, title: 'Maquette', done: true });

    expect(result?.done).toBe(true);
  });

  it('should delete a subtask', () => {
    let done = false;

    service.deleteSubtask(taskId, subtaskId).subscribe(() => (done = true));

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks/sub-1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);

    expect(done).toBe(true);
  });

  it('should surface server errors to the caller without any local state change', () => {
    const errors: unknown[] = [];

    service.createSubtask(taskId, 'Refusée').subscribe({ error: (err) => errors.push(err) });

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks');
    req.flush({ message: 'limite de 50 sous-tâches par tâche atteinte' }, { status: 409, statusText: 'Conflict' });

    expect(errors.length).toBe(1);
    expect((errors[0] as { status: number }).status).toBe(409);
  });
});
