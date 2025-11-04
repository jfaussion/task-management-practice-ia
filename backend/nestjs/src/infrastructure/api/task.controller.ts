import {
  Controller,
  Get,
  Post,
  Put,
  Delete,
  Body,
  Param,
  Query,
  HttpCode,
  HttpStatus,
  Inject,
} from '@nestjs/common';
import { ITaskService, TASK_SERVICE } from '../../application/service/task.service';
import { Task } from '../../domain/model/task.model';
import { CreateTaskDto, UpdateTaskDto } from './dto/task.dto';
import { NotFoundException } from './exceptions/not-found.exception';

@Controller('tasks')
export class TaskController {
  constructor(
    @Inject(TASK_SERVICE)
    private readonly taskService: ITaskService,
  ) {}

  @Get()
  async findAll(
    @Query('status') status?: string,
    @Query('priority') priority?: string,
    @Query('assigneeId') assigneeId?: string,
  ): Promise<Task[]> {
    return this.taskService.findAll({ status, priority, assigneeId });
  }

  @Get(':id')
  async findById(@Param('id') id: string): Promise<Task> {
    const task = await this.taskService.findById(id);
    if (!task) {
      throw new NotFoundException('Task', id);
    }
    return task;
  }

  @Post()
  @HttpCode(HttpStatus.CREATED)
  async create(@Body() createTaskDto: CreateTaskDto): Promise<Task> {
    return this.taskService.create(createTaskDto);
  }

  @Put(':id')
  async update(
    @Param('id') id: string,
    @Body() updateTaskDto: UpdateTaskDto,
  ): Promise<Task> {
    const task = await this.taskService.findById(id);
    if (!task) {
      throw new NotFoundException('Task', id);
    }
    return this.taskService.update(id, updateTaskDto);
  }

  @Get('users/:userId/tasks')
  async findByUserId(@Param('userId') userId: string): Promise<Task[]> {
    return this.taskService.findByUserId(userId);
  }
} 