import { ApiProperty } from '@nestjs/swagger';
import { IsEmail, IsNotEmpty, IsString, MinLength } from 'class-validator';

export class RegisterDto {
  @ApiProperty({
    description: 'The email address of the new user',
    example: 'newuser@skillforge.com',
  })
  @IsEmail({}, { message: 'Invalid email address' })
  readonly email!: string;

  @ApiProperty({
    description: 'The full name of the new user',
    example: 'John Doe',
  })
  @IsString()
  @IsNotEmpty({ message: 'Full name is required' })
  readonly fullName!: string;

  @ApiProperty({
    description: 'The password for the new account',
    example: 'strongpassword123',
    minLength: 6,
  })
  @IsString()
  @MinLength(6, { message: 'Password must be at least 6 characters long' })
  readonly password!: string;
}
