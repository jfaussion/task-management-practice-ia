import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { TaskCardComponent } from './task-card.component';
import { SubtaskService } from '../../application/service/subtask.service';
import { Task } from '../../domain/model/task.model';
import { Subtask } from '../../domain/model/subtask.model';

describe('TaskCardComponent', () => {
  let component: TaskCardComponent;
  let fixture: ComponentFixture<TaskCardComponent>;
  let subtaskServiceSpy: jasmine.SpyObj<SubtaskService>;

  const taskId = 'task-1';

  function makeTask(subtasks: Subtask[]): Task {
    return { id: taskId, title: 'Tâche de test', status: 'TODO', subtasks };
  }

  function makeSubtask(id: string, title: string, done = false): Subtask {
    return { id, taskId, title, done };
  }

  beforeEach(async () => {
    subtaskServiceSpy = jasmine.createSpyObj<SubtaskService>('SubtaskService', [
      'getSubtasks',
      'createSubtask',
      'renameSubtask',
      'setSubtaskDone',
      'deleteSubtask'
    ]);

    await TestBed.configureTestingModule({
      imports: [TaskCardComponent],
      providers: [{ provide: SubtaskService, useValue: subtaskServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskCardComponent);
    component = fixture.componentInstance;
  });

  function render(task: Task): void {
    fixture.componentRef.setInput('task', task);
    fixture.detectChanges();
  }

  function items(): HTMLElement[] {
    return Array.from(fixture.nativeElement.querySelectorAll('[data-testid="subtask-item"]'));
  }

  async function addSubtask(subtask: Subtask): Promise<void> {
    subtaskServiceSpy.createSubtask.and.returnValue(of(subtask));
    fixture.nativeElement.querySelector('[data-testid="add-subtask"]').click();
    fixture.detectChanges();
    // ngModel writes the initial value asynchronously.
    await fixture.whenStable();
    const input = fixture.nativeElement.querySelector('[data-testid="subtask-title-input"]') as HTMLInputElement;
    input.value = subtask.title;
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    (fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement).click();
    fixture.detectChanges();
  }

  // --- US1: thumbnails and CRUD buttons ---

  it('should render one thumbnail with a checkbox per subtask', () => {
    render(makeTask([makeSubtask('s1', 'First'), makeSubtask('s2', 'Second')]));

    expect(items().length).toBe(2);
    expect(items()[0].querySelector('input[type="checkbox"]')).toBeTruthy();
    expect(items()[0].textContent).toContain('First');
  });

  it('should open the subtask form when clicking "+"', () => {
    render(makeTask([]));

    fixture.nativeElement.querySelector('[data-testid="add-subtask"]').click();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('form')).toBeTruthy();
  });

  it('should open the subtask form prefilled when clicking the pencil', async () => {
    render(makeTask([makeSubtask('s1', 'Maquette')]));

    (items()[0].querySelector('[data-testid="edit-subtask"]') as HTMLElement).click();
    fixture.detectChanges();
    await fixture.whenStable();

    const input = fixture.nativeElement.querySelector('[data-testid="subtask-title-input"]') as HTMLInputElement;
    expect(input.value).toBe('Maquette');
  });

  it('should delete a subtask after confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    subtaskServiceSpy.deleteSubtask.and.returnValue(of(undefined));
    render(makeTask([makeSubtask('s1', 'Doomed')]));

    (items()[0].querySelector('[data-testid="delete-subtask"]') as HTMLElement).click();
    fixture.detectChanges();

    expect(window.confirm).toHaveBeenCalled();
    expect(subtaskServiceSpy.deleteSubtask).toHaveBeenCalledWith(taskId, 's1');
    expect(items().length).toBe(0);
  });

  it('should not delete a subtask when confirmation is cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    render(makeTask([makeSubtask('s1', 'Safe')]));

    (items()[0].querySelector('[data-testid="delete-subtask"]') as HTMLElement).click();
    fixture.detectChanges();

    expect(subtaskServiceSpy.deleteSubtask).not.toHaveBeenCalled();
    expect(items().length).toBe(1);
  });

  it('should keep the previous state when deletion fails', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    subtaskServiceSpy.deleteSubtask.and.returnValue(throwError(() => ({ status: 500 })));
    render(makeTask([makeSubtask('s1', 'Stuck')]));

    (items()[0].querySelector('[data-testid="delete-subtask"]') as HTMLElement).click();
    fixture.detectChanges();

    expect(items().length).toBe(1);
    expect(component.listError()).toContain('erreur');
  });

  it('should collapse and expand the subtask list', () => {
    render(makeTask([makeSubtask('s1', 'First')]));

    fixture.nativeElement.querySelector('[data-testid="collapse-toggle"]').click();
    fixture.detectChanges();
    expect(items().length).toBe(0);

    fixture.nativeElement.querySelector('[data-testid="collapse-toggle"]').click();
    fixture.detectChanges();
    expect(items().length).toBe(1);
  });

  it('should auto-expand the list when adding a subtask', async () => {
    render(makeTask([makeSubtask('s1', 'First')]));
    fixture.nativeElement.querySelector('[data-testid="collapse-toggle"]').click();
    fixture.detectChanges();
    expect(items().length).toBe(0);

    await addSubtask(makeSubtask('s2', 'Second'));

    expect(items().length).toBe(2);
  });

  it('should not offer a "+" on a subtask thumbnail (one level only)', () => {
    render(makeTask([makeSubtask('s1', 'First')]));

    const item = items()[0];
    const addButtons = item.querySelectorAll('[data-testid="add-subtask"]');
    expect(addButtons.length).toBe(0);
  });

  // --- US2: progress bar ---

  it('should not render a progress bar without subtasks', () => {
    render(makeTask([]));

    expect(fixture.nativeElement.querySelector('[data-testid="progress-bar"]')).toBeNull();
  });

  it('should render 0 % when no subtask is done', () => {
    render(makeTask([makeSubtask('s1', 'A'), makeSubtask('s2', 'B')]));

    const label = fixture.nativeElement.querySelector('[data-testid="progress-label"]');
    expect(label.textContent).toContain('0 %');
  });

  it('should render 50 % with 2 subtasks done out of 4', () => {
    render(makeTask([
      makeSubtask('s1', 'A', true),
      makeSubtask('s2', 'B', true),
      makeSubtask('s3', 'C'),
      makeSubtask('s4', 'D')
    ]));

    const label = fixture.nativeElement.querySelector('[data-testid="progress-label"]');
    expect(label.textContent).toContain('50 %');
  });

  it('should render 100 % when all subtasks are done', () => {
    render(makeTask([makeSubtask('s1', 'A', true), makeSubtask('s2', 'B', true)]));

    const label = fixture.nativeElement.querySelector('[data-testid="progress-label"]');
    expect(label.textContent).toContain('100 %');
  });

  it('should render 75 % with 3 subtasks done out of 4', () => {
    render(makeTask([
      makeSubtask('s1', 'A', true),
      makeSubtask('s2', 'B', true),
      makeSubtask('s3', 'C', true),
      makeSubtask('s4', 'D')
    ]));

    const label = fixture.nativeElement.querySelector('[data-testid="progress-label"]');
    expect(label.textContent).toContain('75 %');
  });

  it('should round 1 done out of 3 to 33 %', () => {
    render(makeTask([makeSubtask('s1', 'A', true), makeSubtask('s2', 'B'), makeSubtask('s3', 'C')]));

    const label = fixture.nativeElement.querySelector('[data-testid="progress-label"]');
    expect(label.textContent).toContain('33 %');
  });

  it('should show 0 % / 100 % for a single subtask', () => {
    render(makeTask([makeSubtask('s1', 'Alone')]));
    expect(fixture.nativeElement.querySelector('[data-testid="progress-label"]').textContent).toContain('0 %');

    subtaskServiceSpy.setSubtaskDone.and.returnValue(of(makeSubtask('s1', 'Alone', true)));
    const checkbox = items()[0].querySelector('input[type="checkbox"]') as HTMLInputElement;
    checkbox.click();
    fixture.detectChanges();

    expect(subtaskServiceSpy.setSubtaskDone).toHaveBeenCalledWith(taskId, 's1', true);
    expect(fixture.nativeElement.querySelector('[data-testid="progress-label"]').textContent).toContain('100 %');
  });

  it('should recompute the progress immediately when toggling a subtask', () => {
    render(makeTask([makeSubtask('s1', 'A'), makeSubtask('s2', 'B')]));
    expect(fixture.nativeElement.querySelector('[data-testid="progress-label"]').textContent).toContain('0 %');

    subtaskServiceSpy.setSubtaskDone.and.returnValue(of(makeSubtask('s1', 'A', true)));
    (items()[0].querySelector('input[type="checkbox"]') as HTMLInputElement).click();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="progress-label"]').textContent).toContain('50 %');
  });

  it('should recompute the progress immediately when deleting a subtask', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    subtaskServiceSpy.deleteSubtask.and.returnValue(of(undefined));
    render(makeTask([
      makeSubtask('s1', 'A', true),
      makeSubtask('s2', 'B'),
      makeSubtask('s3', 'C')
    ]));
    expect(fixture.nativeElement.querySelector('[data-testid="progress-label"]').textContent).toContain('33 %');

    (items()[0].querySelector('[data-testid="delete-subtask"]') as HTMLElement).click();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="progress-label"]').textContent).toContain('0 %');
  });

  it('should keep the checkbox unchecked when the toggle fails', () => {
    subtaskServiceSpy.setSubtaskDone.and.returnValue(throwError(() => ({ status: 500 })));
    render(makeTask([makeSubtask('s1', 'A')]));

    const checkbox = items()[0].querySelector('input[type="checkbox"]') as HTMLInputElement;
    checkbox.click();
    fixture.detectChanges();

    expect(subtaskServiceSpy.setSubtaskDone).toHaveBeenCalled();
    expect((items()[0].querySelector('input[type="checkbox"]') as HTMLInputElement).checked).toBe(false);
  });

  it('should render the progress bar between the task fields and the subtask list', () => {
    render(makeTask([makeSubtask('s1', 'A')]));

    const card = fixture.nativeElement;
    const progressBar = card.querySelector('[data-testid="progress-bar"]');
    const firstItem = card.querySelector('[data-testid="subtask-item"]');
    const statusSelect = card.querySelector('select');

    expect(progressBar).toBeTruthy();
    // FR-007: progress between task fields and the thumbnail list.
    expect(statusSelect.compareDocumentPosition(progressBar) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy();
    expect(progressBar.compareDocumentPosition(firstItem) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy();
  });
});
