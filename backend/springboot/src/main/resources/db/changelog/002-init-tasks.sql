-- Insert initial tasks
INSERT INTO tasks (id, title, description, status, priority, due_date, created_at, updated_at, assignee_id) VALUES
  ('550e8400-e29b-41d4-a716-446655440010', 'Task 1', 'Description for task 1', 'TODO', 'High', '2023-12-01', now(), now(), '550e8400-e29b-41d4-a716-446655440000'),
  ('550e8400-e29b-41d4-a716-446655440011', 'Task 2', 'Description for task 2', 'IN_PROGRESS', 'Medium', '2023-12-05', now(), now(), '550e8400-e29b-41d4-a716-446655440001'),
  ('550e8400-e29b-41d4-a716-446655440012', 'Task 3', 'Description for task 3', 'DONE', 'Low', '2023-12-10', now(), now(), '550e8400-e29b-41d4-a716-446655440002'),
  ('550e8400-e29b-41d4-a716-446655440013', 'Task 4', 'Description for task 4', 'TODO', 'Medium', '2023-12-15', now(), now(), '550e8400-e29b-41d4-a716-446655440003'),
  ('550e8400-e29b-41d4-a716-446655440014', 'Task 5', 'Description for task 5', 'IN_PROGRESS', 'High', '2023-12-20', now(), now(), '550e8400-e29b-41d4-a716-446655440004'); 