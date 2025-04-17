import { UserRole } from '../../../domain/model/user.model';

export class CreateUserDto {
  username: string;
  email?: string;
  role?: UserRole;
}

export class UpdateUserDto {
  username?: string;
  email?: string;
  role?: UserRole;
} 