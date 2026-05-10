import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsBoolean, IsOptional, IsString } from 'class-validator';

export class CreateInstructorSubscriptionDto {
  @ApiProperty({
    description: 'Confirms that the payment was accepted by the user.',
    example: true,
  })
  @IsBoolean()
  readonly isConfirmed!: boolean;

  @ApiPropertyOptional({
    description: 'Optional plan identifier. The backend owns the actual plan values.',
    example: 'INSTRUCTOR_STANDARD_PLAN',
  })
  @IsOptional()
  @IsString()
  readonly planCode?: string;
}
