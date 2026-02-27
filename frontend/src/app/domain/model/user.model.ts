export type UserRole = 'USER' | 'ADMIN';

export interface User {
  id: string;
  username: string;
  email?: string;
  role: UserRole;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateUserRequest {
  username: string;
  email?: string;
  role?: UserRole;
}