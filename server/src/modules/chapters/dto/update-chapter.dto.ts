import { IsNumber, IsOptional, IsString } from 'class-validator';

export class UpdateChapterDto {
  @IsOptional()
  @IsString()
  title?: string;

  @IsOptional()
  @IsNumber()
  orderIndex?: number;
}
