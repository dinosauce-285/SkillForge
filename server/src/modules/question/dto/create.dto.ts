import {
  IsInt,
  IsOptional,
  IsPositive,
  IsString,
  IsArray,
  ValidateNested,
} from 'class-validator';
import { Type } from 'class-transformer';
import { CreateAnswerChoiceDto } from 'src/modules/answer_choices/dto/create.dto';

export class CreateQuestionDto {
  @IsOptional()
  @IsString()
  quizId?: string;

  @IsString()
  content!: string;

  @IsOptional()
  @IsString()
  explanation?: string;

  @IsInt()
  @IsPositive()
  orderIndex!: number;

  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CreateAnswerChoiceDto)
  choices!: CreateAnswerChoiceDto[];
}
