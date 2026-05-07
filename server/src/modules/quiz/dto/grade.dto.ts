import { IsNotEmpty, IsNumber, IsString, IsArray } from 'class-validator';

export class GradeEssayDto {
  @IsArray()
  @IsNotEmpty()
  questionGrades: { questionId: string; points: number }[];

  @IsString()
  @IsNotEmpty()
  feedback: string;
}
