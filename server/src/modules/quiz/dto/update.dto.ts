import { PartialType, OmitType } from '@nestjs/mapped-types';
import { CreateQuizDto } from './create.dto';

export class UpdateQuizDto extends PartialType(
  OmitType(CreateQuizDto, ['chapterId', 'questions'] as const)
) {}
