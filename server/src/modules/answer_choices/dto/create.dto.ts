import {
  IsString,
  IsInt,
  IsBoolean,
  IsPositive,
  IsOptional,
} from 'class-validator';

export class CreateAnswerChoiceDto {
  @IsOptional()
  @IsString()
  questionId?: string;

  @IsString()
  content!: string;

  @IsBoolean()
  isCorrect!: boolean;

  @IsInt()
  @IsPositive()
  orderIndex!: number;
}
