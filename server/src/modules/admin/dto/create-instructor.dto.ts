import { IsEmail, IsNotEmpty, IsString, MinLength } from 'class-validator';

export class CreateInstructorDto {
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @IsString()
  @IsNotEmpty()
  fullName: string;
}
