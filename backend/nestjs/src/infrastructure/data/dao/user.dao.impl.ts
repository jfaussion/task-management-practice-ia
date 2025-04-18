import { Injectable } from '@nestjs/common';
import { IUserDao } from '../../../application/dao/user.dao';
import { User } from '../../../domain/model/user.model';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class UserDaoImpl implements IUserDao {
  constructor(private readonly prisma: PrismaService) {}

  async findAll(): Promise<User[]> {
    const users = await this.prisma.user.findMany();
    return users.map(this.mapToUser);
  }

  async findById(id: string): Promise<User | null> {
    const user = await this.prisma.user.findUnique({
      where: { id },
    });
    return user ? this.mapToUser(user) : null;
  }

  async create(user: Partial<User>): Promise<User> {
    const createdUser = await this.prisma.user.create({
      data: {
        username: user.username || '',
        email: user.email || '',
        role: user.role || '',
      },
    });
    return this.mapToUser(createdUser);
  }

  async update(id: string, user: Partial<User>): Promise<User> {
    const updatedUser = await this.prisma.user.update({
      where: { id },
      data: {
        username: user.username,
        email: user.email,
        role: user.role,
      },
    });
    return this.mapToUser(updatedUser);
  }

  async delete(id: string): Promise<void> {
    await this.prisma.user.delete({
      where: { id },
    });
  }

  private mapToUser(prismaUser: any): User {
    const user = new User();
    user.id = prismaUser.id;
    user.username = prismaUser.username;
    user.email = prismaUser.email;
    user.role = prismaUser.role;
    user.createdAt = prismaUser.createdAt;
    user.updatedAt = prismaUser.updatedAt;
    return user;
  }
} 