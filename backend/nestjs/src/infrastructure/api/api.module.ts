import { Module } from '@nestjs/common';
import { UserController } from './user.controller';
import { TaskController } from './task.controller';
import { UserServiceImpl } from '../../application/service/impl/user.service.impl';
import { TaskServiceImpl } from '../../application/service/impl/task.service.impl';
import { UserDaoImpl } from '../data/dao/user.dao.impl';
import { TaskDaoImpl } from '../data/dao/task.dao.impl';
import { USER_SERVICE } from '../../application/service/user.service';
import { TASK_SERVICE } from '../../application/service/task.service';
import { USER_DAO } from '../../application/dao/user.dao';
import { TASK_DAO } from '../../application/dao/task.dao';

@Module({
  controllers: [UserController, TaskController],
  providers: [
    {
      provide: USER_SERVICE,
      useClass: UserServiceImpl,
    },
    {
      provide: TASK_SERVICE,
      useClass: TaskServiceImpl,
    },
    {
      provide: USER_DAO,
      useClass: UserDaoImpl,
    },
    {
      provide: TASK_DAO,
      useClass: TaskDaoImpl,
    },
  ],
  exports: [USER_SERVICE, TASK_SERVICE],
})
export class ApiModule {} 