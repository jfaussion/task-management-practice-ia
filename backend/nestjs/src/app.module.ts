import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { PrismaModule } from './infrastructure/data/prisma/prisma.module';
import { ApiModule } from './infrastructure/api/api.module';

@Module({
  imports: [PrismaModule, ApiModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
