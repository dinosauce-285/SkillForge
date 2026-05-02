import {
  IsString,
  IsInt,
  IsPositive,
  IsBoolean,
  IsArray,
  ValidateNested,
} from 'class-validator';
import { Type } from 'class-transformer';
import { CreateQuestionDto } from 'src/modules/question/dto/create.dto';

export class CreateQuizDto {
  @IsString()
  chapterId!: string;

  @IsString()
  title!: string;

  @IsInt()
  @IsPositive()
  timeLimit!: number;

  @IsInt()
  @IsPositive()
  passingScore!: number;

  @IsBoolean()
  randomizeQuestions!: boolean;

  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CreateQuestionDto)
  questions!: CreateQuestionDto[];
}
