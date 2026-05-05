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
import { QuizModule } from './modules/quiz/quiz.module';
import { QuestionModule } from './modules/question/question.module';
import { AnswerChoicesModule } from './modules/answer_choices/answer_choices.module';
import { DashboardModule } from './modules/dashboard/dashboard.module';
import { ReviewModule } from './modules/review/reviews.module';
import { CouponsModule } from './modules/coupons/coupons.module';
import { NotificationsModule } from './modules/notifications/notifications.module';
import { AdminModule } from './modules/admin/admin.module';

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
    QuizModule,
    QuestionModule,
    AnswerChoicesModule,
    DashboardModule,
    ReviewModule,
    CouponsModule,
    NotificationsModule,
    AdminModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
