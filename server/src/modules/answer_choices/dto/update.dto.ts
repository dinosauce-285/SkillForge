import {
  IsString,
  IsInt,
  IsBoolean,
  IsPositive,
  IsOptional,
} from 'class-validator';

export class UpdateAnswerChoiceDto {
  @IsOptional()
  @IsString()
  content?: string;

  @IsOptional()
  @IsBoolean()
  isCorrect?: boolean;

  @IsOptional()
  @IsInt()
  @IsPositive()
  orderIndex?: number;
}
