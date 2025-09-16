-- Test data for user search functionality
INSERT INTO users (id, username, email, role, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'john_doe', 'john.doe@example.com', 'USER', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
('550e8400-e29b-41d4-a716-446655440002', 'jane_smith', 'jane.smith@example.com', 'USER', '2024-01-01 11:00:00', '2024-01-01 11:00:00'),
('550e8400-e29b-41d4-a716-446655440003', 'test_user1', 'test1@example.com', 'USER', '2024-01-01 12:00:00', '2024-01-01 12:00:00'),
('550e8400-e29b-41d4-a716-446655440004', 'test_user2', 'test2@example.com', 'USER', '2024-01-01 13:00:00', '2024-01-01 13:00:00'),
('550e8400-e29b-41d4-a716-446655440005', 'test_admin', 'admin@example.com', 'ADMIN', '2024-01-01 14:00:00', '2024-01-01 14:00:00');