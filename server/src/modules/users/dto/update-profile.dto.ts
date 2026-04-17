import { Transform } from 'class-transformer';
import {
  IsString,
  IsOptional,
  IsArray,
  IsUrl,
  MaxLength,
  MinLength,
} from 'class-validator';

export class UpdateProfileDto {
  @IsOptional()
  @IsString()
  @MinLength(2, { message: 'Full name must contain at least 2 characters' })
  @MaxLength(50)
  fullName?: string;

  @IsOptional()
  @IsUrl({ require_tld: false, require_protocol: false }, { message: 'Invalid avatar URL' })
  avatarUrl?: string;

  @IsOptional()
  @IsArray()
  @IsString({ each: true, message: 'Each skill must be a string' })
  skills?: string[];

  @IsOptional()
  @IsString()
  @MaxLength(1000, { message: 'Learning goals cannot exceed 1000 characters' })
  @Transform(({ value }: { value: string | undefined }) => value?.trim())
  learningGoals?: string;
}
