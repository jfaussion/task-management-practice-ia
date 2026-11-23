import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SubtaskFormComponent } from './subtask-form.component';
import { Subtask } from '../../domain/model/subtask.model';

describe('SubtaskFormComponent', () => {
  let component: SubtaskFormComponent;
  let fixture: ComponentFixture<SubtaskFormComponent>;
  let httpMock: HttpTestingController;

  const taskId = 'task-1';
  const existing: Subtask = { id: 'sub-1', taskId, title: 'Maquette', done: false };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubtaskFormComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(SubtaskFormComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('taskId', taskId);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should render an empty input in creation mode', () => {
    fixture.detectChanges();

    const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
    expect(input.value).toBe('');
  });

  it('should render a prefilled input in edition mode', async () => {
    fixture.componentRef.setInput('subtask', existing);
    fixture.detectChanges();
    // ngModel writes the initial value asynchronously.
    await fixture.whenStable();

    const input = fixture.nativeElement.querySelector('[data-testid="subtask-title-input"]') as HTMLInputElement;
    expect(input.value).toBe('Maquette');
  });

  it('should reject an empty title with a clear message and no request', () => {
    fixture.detectChanges();

    component.title = '   ';
    component.onSubmit();

    expect(component.error()).toBe('Le titre est requis (non vide).');
    httpMock.expectNone('/api/v1/tasks/task-1/subtasks');
  });

  it('should reject a title longer than 200 characters with a clear message and no request', () => {
    fixture.detectChanges();

    component.title = 'a'.repeat(201);
    component.onSubmit();

    expect(component.error()).toBe('Le titre est limité à 200 caractères.');
    httpMock.expectNone('/api/v1/tasks/task-1/subtasks');
  });

  it('should create the subtask and emit it on valid submit', () => {
    let saved: Subtask | undefined;
    fixture.detectChanges();

    component.saved.subscribe((subtask) => (saved = subtask));
    component.title = '  Maquette  ';
    component.onSubmit();

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ title: 'Maquette' });
    req.flush({ id: 'sub-2', taskId, title: 'Maquette', done: false });

    expect(saved?.id).toBe('sub-2');
  });

  it('should rename the subtask when editing', () => {
    fixture.componentRef.setInput('subtask', existing);
    fixture.detectChanges();

    let saved: Subtask | undefined;
    component.saved.subscribe((subtask) => (saved = subtask));
    component.title = 'Nouveau titre';
    component.onSubmit();

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks/sub-1');
    expect(req.request.method).toBe('PUT');
    req.flush({ id: 'sub-1', taskId, title: 'Nouveau titre', done: false });

    expect(saved?.title).toBe('Nouveau titre');
  });

  it('should stay open with a generic error message when the parent task disappeared (404)', () => {
    fixture.detectChanges();

    let savedEmitted = false;
    let cancelledEmitted = false;
    component.saved.subscribe(() => (savedEmitted = true));
    component.cancelled.subscribe(() => (cancelledEmitted = true));
    component.title = 'Maquette';
    component.onSubmit();

    const req = httpMock.expectOne('/api/v1/tasks/task-1/subtasks');
    req.flush({ message: 'ressource introuvable' }, { status: 404, statusText: 'Not Found' });
    fixture.detectChanges();

    expect(savedEmitted).toBe(false);
    expect(cancelledEmitted).toBe(false);
    expect(component.error()).toContain("erreur");
  });

  it('should emit cancelled on cancel', () => {
    let cancelled = false;
    fixture.detectChanges();

    component.cancelled.subscribe(() => (cancelled = true));
    component.onCancel();

    expect(cancelled).toBe(true);
  });
});
