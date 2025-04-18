import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { PrismaModule } from './infrastructure/data/prisma/prisma.module';
import { ApiModule } from './infrastructure/api/api.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),
    PrismaModule,
    ApiModule
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
