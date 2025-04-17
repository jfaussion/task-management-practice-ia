export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN',
}

export class User {
  id: string;
  username: string;
  email?: string;
  role: UserRole;
  createdAt: Date;
  updatedAt: Date;
} 