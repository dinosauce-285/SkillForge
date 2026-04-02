import {
  IsNotEmpty,
  IsNumber,
  IsOptional,
  IsString,
  IsUUID,
} from 'class-validator';

export class CreateLessonDto {
  @IsUUID()
  @IsNotEmpty()
  chapterId: string;

  @IsString()
  @IsNotEmpty()
  title: string;

  @IsNumber()
  @IsOptional()
  orderIndex?: number;
}
