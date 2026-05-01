import {
  IsInt,
  IsOptional,
  IsString,
  IsArray,
  ValidateNested,
  Min,
} from 'class-validator';
import { Type } from 'class-transformer';
import { CreateAnswerChoiceDto } from 'src/modules/answer_choices/dto/create.dto';

export class UpdateQuestionDto {
  @IsOptional()
  @IsString()
  content?: string;

  @IsOptional()
  @IsString()
  explanation?: string;

  @IsOptional()
  @IsInt()
  @Min(0)
  orderIndex?: number;

  @IsOptional()
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CreateAnswerChoiceDto)
  choices?: CreateAnswerChoiceDto[];
}
