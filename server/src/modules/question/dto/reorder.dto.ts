import { IsArray, IsString } from 'class-validator';

export class ReorderQuestionsDto {
  @IsArray()
  @IsString({ each: true })
  orderedQuestionIds!: string[];
}
