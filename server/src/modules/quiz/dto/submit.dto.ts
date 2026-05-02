import { IsObject, IsString } from 'class-validator';

export class SubmitQuizDto {
  @IsObject()
  answers: Record<string, string>; // questionId -> choiceId
}
