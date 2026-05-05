import { IsEnum, IsOptional } from 'class-validator';
import { CourseStatus, CourseLevel } from '@prisma/client';

export class ModerateCourseDto {
  @IsEnum(CourseStatus)
  status: CourseStatus;

  @IsOptional()
  @IsEnum(CourseLevel)
  level?: CourseLevel;
}
