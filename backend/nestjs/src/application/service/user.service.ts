import { User } from '../../domain/model/user.model';

export const USER_SERVICE = 'USER_SERVICE';

export interface IUserService {
  findAll(): Promise<User[]>;
  findById(id: string): Promise<User | null>;
  create(user: Partial<User>): Promise<User>;
  update(id: string, user: Partial<User>): Promise<User>;
  delete(id: string): Promise<void>;
} 