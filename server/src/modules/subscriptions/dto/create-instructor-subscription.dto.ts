import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsBoolean, IsOptional, IsString } from 'class-validator';

export class CreateInstructorSubscriptionDto {
  @ApiProperty({
    description: 'Confirms that the mock payment was accepted by the user.',
    example: true,
  })
  @IsBoolean()
  readonly mockPaymentConfirmed!: boolean;

  @ApiPropertyOptional({
    description: 'Optional mock plan identifier. The backend owns the actual plan values.',
    example: 'INSTRUCTOR_MOCK_PLAN',
  })
  @IsOptional()
  @IsString()
  readonly planCode?: string;
}
