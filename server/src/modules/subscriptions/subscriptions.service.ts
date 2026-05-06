import {
  BadRequestException,
  ConflictException,
  ForbiddenException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import {
  InstructorSubscriptionPaymentStatus,
  InstructorSubscriptionStatus,
  Prisma,
  Role,
} from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { CreateInstructorSubscriptionDto } from './dto/create-instructor-subscription.dto';

const INSTRUCTOR_MOCK_PLAN_CODE = 'INSTRUCTOR_MOCK_PLAN';
const INSTRUCTOR_MOCK_PLAN_AMOUNT = 9.99;
const INSTRUCTOR_MOCK_PLAN_CURRENCY = 'USD';

export interface InstructorSubscriptionResponse {
  readonly message: string;
  readonly subscription: {
    readonly id: string;
    readonly planCode: string;
    readonly status: InstructorSubscriptionStatus;
    readonly paymentStatus: InstructorSubscriptionPaymentStatus;
    readonly amount: string;
    readonly currency: string;
    readonly mockPaymentReference: string;
    readonly startedAt: Date;
    readonly expiresAt: Date | null;
  };
  readonly user: {
    readonly id: string;
    readonly email: string;
    readonly fullName: string;
    readonly role: Role;
  };
}

@Injectable()
export class SubscriptionsService {
  constructor(private readonly prisma: PrismaService) {}

  async createInstructorSubscription(
    userId: string,
    dto: CreateInstructorSubscriptionDto,
  ): Promise<InstructorSubscriptionResponse> {
    if (!dto.mockPaymentConfirmed) {
      throw new BadRequestException('Mock payment must be confirmed.');
    }

    try {
      const result = await this.prisma.$transaction(async (tx) => {
        const user = await tx.user.findUnique({
          where: { id: userId },
          select: {
            id: true,
            email: true,
            fullName: true,
            role: true,
            isActive: true,
            lockedUntil: true,
          },
        });

        if (!user) {
          throw new UnauthorizedException('User not found.');
        }

        if (user.role === Role.ADMIN) {
          throw new ForbiddenException('Admins cannot buy instructor subscriptions.');
        }

        if (user.role === Role.INSTRUCTOR) {
          throw new ConflictException('User is already an instructor.');
        }

        if (!user.isActive || (user.lockedUntil && user.lockedUntil > new Date())) {
          throw new ForbiddenException('Account is inactive or locked.');
        }

        const activeSubscription = await tx.instructorSubscription.findFirst({
          where: {
            userId,
            status: InstructorSubscriptionStatus.ACTIVE,
          },
        });

        if (activeSubscription) {
          throw new ConflictException('An active instructor subscription already exists.');
        }

        const subscription = await tx.instructorSubscription.create({
          data: {
            userId,
            planCode: INSTRUCTOR_MOCK_PLAN_CODE,
            amount: INSTRUCTOR_MOCK_PLAN_AMOUNT,
            currency: INSTRUCTOR_MOCK_PLAN_CURRENCY,
            status: InstructorSubscriptionStatus.ACTIVE,
            paymentStatus: InstructorSubscriptionPaymentStatus.SUCCEEDED,
          },
          select: {
            id: true,
            planCode: true,
            status: true,
            paymentStatus: true,
            amount: true,
            currency: true,
            mockPaymentReference: true,
            startedAt: true,
            expiresAt: true,
          },
        });

        const upgradedUser = await tx.user.update({
          where: { id: userId },
          data: { role: Role.INSTRUCTOR },
          select: {
            id: true,
            email: true,
            fullName: true,
            role: true,
          },
        });

        return { subscription, user: upgradedUser };
      });

      return {
        message: 'Instructor subscription activated.',
        subscription: {
          ...result.subscription,
          amount: result.subscription.amount.toString(),
        },
        user: result.user,
      };
    } catch (error) {
      if (
        error instanceof Prisma.PrismaClientKnownRequestError &&
        error.code === 'P2002'
      ) {
        throw new ConflictException('An active instructor subscription already exists.');
      }

      throw error;
    }
  }
}
