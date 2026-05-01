import {
  IsString,
  IsInt,
  IsPositive,
  IsBoolean,
  IsArray,
  IsOptional,
  ValidateNested,
  IsNumber,
} from 'class-validator';
import { Type } from 'class-transformer';
import { CreateQuestionDto } from 'src/modules/question/dto/create.dto';

export class CreateQuizDto {
  @IsString()
  chapterId!: string;

  @IsOptional()
  @IsString()
  title?: string;

  @IsInt()
  @IsPositive()
  timeLimit!: number;

  @IsNumber()
  @IsPositive()
  passingScore!: number;

  @IsBoolean()
  randomizeQuestions!: boolean;

  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CreateQuestionDto)
  questions!: CreateQuestionDto[];
}
