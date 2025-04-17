import { Injectable, Inject } from '@nestjs/common';
import { IUserService } from '../user.service';
import { IUserDao, USER_DAO } from '../../dao/user.dao';
import { User } from '../../../domain/model/user.model';

@Injectable()
export class UserServiceImpl implements IUserService {
  constructor(
    @Inject(USER_DAO)
    private readonly userDao: IUserDao,
  ) {}

  async findAll(): Promise<User[]> {
    return this.userDao.findAll();
  }

  async findById(id: string): Promise<User | null> {
    return this.userDao.findById(id);
  }

  async create(user: Partial<User>): Promise<User> {
    return this.userDao.create(user);
  }

  async update(id: string, user: Partial<User>): Promise<User> {
    return this.userDao.update(id, user);
  }

  async delete(id: string): Promise<void> {
    return this.userDao.delete(id);
  }
} 