import {
  Body,
  Controller,
  Delete,
  Param,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import { Role } from '@prisma/client';
import { Roles } from '../auth/decorators/roles.decorator'; // Đổi đường dẫn cho đúng project của bạn
import { CurrentUser } from '../auth/decorators/current-user.decorator'; // Đổi đường dẫn
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard'; // Đổi đường dẫn
import { ChaptersService } from './chapters.service';
import { CreateChapterDto } from './dto/create-chapter.dto';
import { UpdateChapterDto } from './dto/update-chapter.dto';

@UseGuards(JwtAuthGuard)
@Roles(Role.INSTRUCTOR, Role.ADMIN)
@Controller('chapters')
export class ChaptersController {
  constructor(private readonly chaptersService: ChaptersService) {}

  @Post()
  create(@CurrentUser() user: any, @Body() createChapterDto: CreateChapterDto) {
    return this.chaptersService.create(user, createChapterDto);
  }

  @Patch(':id')
  update(
    @Param('id') id: string,
    @CurrentUser() user: any,
    @Body() updateChapterDto: UpdateChapterDto,
  ) {
    return this.chaptersService.update(id, user, updateChapterDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string, @CurrentUser() user: any) {
    return this.chaptersService.remove(id, user);
  }
}
