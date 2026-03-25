import { CourseLevel, CourseStatus } from '@prisma/client';
import { Transform, Type } from 'class-transformer';
import {
  IsArray,
  IsBoolean,
  IsEnum,
  IsNumber,
  IsOptional,
  IsString,
  IsUrl,
  IsUUID,
  Min,
} from 'class-validator';

export class UpdateCourseDto {
  @IsOptional()
  @IsString()
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  title?: string;

  @IsOptional()
  @IsUUID()
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  categoryId?: string;

  @IsOptional()
  @IsString()
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  subtitle?: string;

  @IsOptional()
  @IsString()
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  summary?: string;

  @IsOptional()
  @IsUrl()
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  thumbnailUrl?: string;

  @IsOptional()
  @IsUrl()
  @Transform(({ value }) => (typeof value === 'string' ? value.trim() : value))
  promoVideoUrl?: string;

  @IsOptional()
  @Type(() => Number)
  @IsNumber({ allowInfinity: false, allowNaN: false })
  @Min(0)
  price?: number;

  @IsOptional()
  @IsBoolean()
  isFree?: boolean;

  @IsOptional()
  @IsEnum(CourseLevel)
  level?: CourseLevel;

  @IsOptional()
  @IsEnum(CourseStatus)
  status?: CourseStatus;

  @IsOptional()
  @IsArray()
  @IsUUID('4', { each: true })
  tagIds?: string[];
}
