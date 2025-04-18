import { Injectable, OnModuleInit, OnModuleDestroy, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class PrismaService extends PrismaClient implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(PrismaService.name);

  constructor(private readonly configService: ConfigService) {
    super({
      log: [
        { emit: 'event', level: 'query' },
        { emit: 'event', level: 'error' },
        { emit: 'event', level: 'info' },
        { emit: 'event', level: 'warn' },
      ],
    });

    this.logger.log('PrismaService constructor called');
    this.logger.log(`Database URL: ${this.configService.get('DATABASE_URL')}`);
    this.logger.log(`Prisma Client output path: ${process.cwd()}/generated/prisma`);
  }

  async onModuleInit() {
    this.logger.log('Initializing PrismaService...');
    try {
      await this.$connect();
      this.logger.log('Successfully connected to database');
      
      // Check if database is initialized
      const tableCount = await this.$queryRaw`
        SELECT COUNT(*) as count FROM sqlite_master WHERE type='table'
      `;
      
      if (tableCount === 0) {
        this.logger.log('Database is empty, initializing...');
        await this.initializeDatabase();
      }
    } catch (error) {
      this.logger.error('Failed to connect to database:', error);
      throw error;
    }
  }

  async onModuleDestroy() {
    this.logger.log('Disconnecting from database...');
    await this.$disconnect();
    this.logger.log('Successfully disconnected from database');
  }

  private async initializeDatabase() {
    try {
      // Create tables based on schema
      await this.$executeRaw`
        CREATE TABLE IF NOT EXISTS "User" (
          "id" TEXT NOT NULL PRIMARY KEY,
          "username" TEXT NOT NULL UNIQUE,
          "email" TEXT,
          "role" TEXT NOT NULL,
          "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
          "updatedAt" DATETIME NOT NULL
        );

        CREATE TABLE IF NOT EXISTS "Task" (
          "id" TEXT NOT NULL PRIMARY KEY,
          "title" TEXT NOT NULL,
          "description" TEXT,
          "status" TEXT NOT NULL,
          "priority" TEXT,
          "dueDate" DATETIME,
          "assigneeId" TEXT,
          "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
          "updatedAt" DATETIME NOT NULL,
          FOREIGN KEY ("assigneeId") REFERENCES "User"("id") ON DELETE SET NULL ON UPDATE CASCADE
        );

        CREATE UNIQUE INDEX IF NOT EXISTS "Task_title_assigneeId_key" ON "Task"("title", "assigneeId");
      `;
      
      this.logger.log('Database initialized successfully');
    } catch (error) {
      this.logger.error('Failed to initialize database:', error);
      throw error;
    }
  }
} 