-- Insert initial users
INSERT INTO users (id, username, role, email, created_at) VALUES
  ('550e8400-e29b-41d4-a716-446655440000', 'alice', 'ADMIN', 'alice@example.com', '2025-03-21T18:55:58.79013'),
  ('550e8400-e29b-41d4-a716-446655440001', 'bob', 'USER', 'bob@example.com', '2025-03-21T18:55:58.79013'),
  ('550e8400-e29b-41d4-a716-446655440002', 'charlie', 'USER', 'charlie@example.com', '2025-03-21T18:55:58.79013'),
  ('550e8400-e29b-41d4-a716-446655440003', 'david', 'USER', 'david@example.com', '2025-03-21T18:55:58.79013'),
  ('550e8400-e29b-41d4-a716-446655440004', 'eve', 'ADMIN', 'eve@example.com', '2025-03-21T18:55:58.79013'); 