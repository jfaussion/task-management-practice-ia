-- Insert initial users
INSERT INTO users (id, username, role, email, created_at) VALUES
  ('550e8400-e29b-41d4-a716-446655440000', 'alice', 'ADMIN', 'alice@example.com', now()),
  ('550e8400-e29b-41d4-a716-446655440001', 'bob', 'USER', 'bob@example.com', now()),
  ('550e8400-e29b-41d4-a716-446655440002', 'charlie', 'USER', 'charlie@example.com', now()),
  ('550e8400-e29b-41d4-a716-446655440003', 'david', 'USER', 'david@example.com', now()),
  ('550e8400-e29b-41d4-a716-446655440004', 'eve', 'ADMIN', 'eve@example.com', now()); 