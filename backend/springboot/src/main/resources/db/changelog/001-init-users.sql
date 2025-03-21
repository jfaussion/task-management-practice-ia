-- Insert initial users
INSERT INTO users (id, username, role, email, created_at) VALUES
  ('uuid-1', 'alice', 'ADMIN', 'alice@example.com', now()),
  ('uuid-2', 'bob', 'USER', 'bob@example.com', now()),
  ('uuid-3', 'charlie', 'USER', 'charlie@example.com', now()),
  ('uuid-4', 'david', 'USER', 'david@example.com', now()),
  ('uuid-5', 'eve', 'ADMIN', 'eve@example.com', now()); 