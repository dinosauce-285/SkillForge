import { PartialType } from '@nestjs/mapped-types'; // Hoặc '@nestjs/swagger' nếu bạn dùng Swagger
import { CreateChapterDto } from './create-chapter.dto';

export class UpdateChapterDto extends PartialType(CreateChapterDto) {}
