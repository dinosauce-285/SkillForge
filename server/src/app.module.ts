import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { PrismaModule } from './modules/prisma/prisma.module';
import { AuthModule } from './modules/auth/auth.module';
import { UsersModule } from './modules/users/users.module';
import { OrderModule } from './modules/order/order.module';
import { CoursesModule } from './modules/courses/courses.module';
import { CategoriesModule } from './modules/categories/categories.module';
import { FavoriteModule } from './modules/favorite/favorite.module';
import { LessonsModule } from './modules/lessons/lessons.module';
import { ChaptersModule } from './modules/chapters/chapters.module';
import { ProgressModule } from './modules/progress/progress.module';
import { DiscussionsModule } from './modules/discussions/discussions.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      envFilePath: '.env',
      isGlobal: true,
    }),
    PrismaModule,
    AuthModule,
    UsersModule,
    OrderModule,
    CoursesModule,
    CategoriesModule,
    FavoriteModule,
    ChaptersModule,
    LessonsModule,
    ProgressModule,
    DiscussionsModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
