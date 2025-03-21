-- Insert initial tasks
INSERT INTO tasks (id, title, description, status, priority, due_date, created_at, updated_at, assignee_id) VALUES
  ('task-uuid-1', 'Task 1', 'Description for task 1', 'TODO', 'High', '2023-12-01', now(), now(), 'uuid-1'),
  ('task-uuid-2', 'Task 2', 'Description for task 2', 'IN_PROGRESS', 'Medium', '2023-12-05', now(), now(), 'uuid-2'),
  ('task-uuid-3', 'Task 3', 'Description for task 3', 'DONE', 'Low', '2023-12-10', now(), now(), 'uuid-3'),
  ('task-uuid-4', 'Task 4', 'Description for task 4', 'TODO', 'Medium', '2023-12-15', now(), now(), 'uuid-4'),
  ('task-uuid-5', 'Task 5', 'Description for task 5', 'IN_PROGRESS', 'High', '2023-12-20', now(), now(), 'uuid-5'); 