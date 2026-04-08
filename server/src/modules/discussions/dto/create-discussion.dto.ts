import { IsString, IsNotEmpty, IsOptional, IsUUID } from 'class-validator';

export class CreateDiscussionDto {
  @IsString()
  @IsNotEmpty()
  content: string;

  @IsOptional()
  @IsUUID()
  parentId?: string;
}
