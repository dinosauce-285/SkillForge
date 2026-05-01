import {
  IsInt,
  IsPositive,
  IsBoolean,
  IsOptional,
  IsString,
  IsNumber,
} from 'class-validator';

export class UpdateQuizDto {
  @IsOptional()
  @IsString()
  title?: string;

  @IsOptional()
  @IsInt()
  @IsPositive()
  timeLimit?: number;

  @IsOptional()
  @IsNumber()
  @IsPositive()
  passingScore?: number;

  @IsOptional()
  @IsBoolean()
  randomizeQuestions?: boolean;
}
