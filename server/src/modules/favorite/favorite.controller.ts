import {
  Controller,
  Post,
  Delete,
  Get,
  Param,
  UseGuards,
  Body,
} from '@nestjs/common';
import { FavoriteService } from './favorite.service';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';

@Controller('favorites')
@UseGuards(JwtAuthGuard)
export class FavoriteController {
  constructor(private readonly favoriteService: FavoriteService) {}

  @Post()
  async addFavorite(
    @CurrentUser('id') userId: string,
    @Body('courseId') courseId: string,
  ) {
    return this.favoriteService.addFavorite(userId, courseId);
  }

  @Delete(':courseId')
  async removeFavorite(
    @CurrentUser('id') userId: string,
    @Param('courseId') courseId: string,
  ) {
    return this.favoriteService.removeFavorite(userId, courseId);
  }

  @Get()
  async getFavorites(@CurrentUser('id') userId: string) {
    return this.favoriteService.getFavorites(userId);
  }
}
