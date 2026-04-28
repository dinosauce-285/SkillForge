import { Module } from '@nestjs/common';
import { CoursesController } from './courses.controller';
import { CoursesService } from './courses.service';
import { PrismaModule } from '../prisma/prisma.module';
import { AuthModule } from '../auth/auth.module';
import { ProgressModule } from '../progress/progress.module';

@Module({
  imports: [PrismaModule, AuthModule, ProgressModule],
  controllers: [CoursesController],
  providers: [CoursesService],
})
export class CoursesModule {}
