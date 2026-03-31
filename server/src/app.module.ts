import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { PrismaModule } from './modules/prisma/prisma.module';
import { AuthModule } from './modules/auth/auth.module';
import { UsersModule } from './modules/users/users.module';
import { OrderModule } from './modules/order/order.module';
import { CoursesModule } from './modules/courses/courses.module';
import { FavoriteModule } from './modules/favorite/favorite.module';

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
    FavoriteModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
