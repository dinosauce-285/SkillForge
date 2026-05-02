import { PartialType } from '@nestjs/mapped-types';
import { CreateQuestionDto } from './create.dto';

export class UpdateQuestionDto extends PartialType(CreateQuestionDto) {}
