import { IsNumber, IsPositive, IsString } from 'class-validator';

export class CreateOrderDto {
  @IsString()
  courseId: string;

  @IsNumber()
  @IsPositive()
  amount: number;
}
