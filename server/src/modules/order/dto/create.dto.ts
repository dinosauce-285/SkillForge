import { IsNumber, IsPositive, IsString } from 'class-validator';

export class CreateOrderDto {
  @IsString()
  userId: string;

  @IsString()
  courseId: string;

  @IsNumber()
  @IsPositive()
  amount: number;
}
