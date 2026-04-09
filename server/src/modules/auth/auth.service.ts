import { BadRequestException, Injectable, UnauthorizedException, ForbiddenException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { PrismaService } from '../prisma/prisma.service';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';
import { ConfigService } from '@nestjs/config';
import * as bcrypt from 'bcrypt';
import { JwtRefreshPayload } from './strategies/jwt-refresh.strategy';

/**
 * Security Configuration Constants
 */
const MAX_LOGIN_ATTEMPTS = 5; // Lock account after 5 failed attempts
const ACCOUNT_LOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutes
const BCRYPT_ROUNDS = 10;

// RO-RO Strongly Typed Responses - Ensure Immutability
export interface AuthUserResponse {
  readonly id: string;
  readonly email: string;
  readonly fullName: string;
  readonly role: string;
}

export interface AuthTokensResponse {
  readonly message: string;
  readonly accessToken: string;
  readonly refreshToken: string;
  readonly user: AuthUserResponse;
}

@Injectable()
export class AuthService {
  constructor(
    private readonly prisma: PrismaService,
    private readonly jwtService: JwtService,
    private readonly configService: ConfigService,
  ) {}

  /**
   * Generates both Access and Refresh tokens
   * @param payload User data payload
   * @returns Tokens Object
   */
  private async generateTokens(payload: { sub: string, email: string, role: string }) {
    // Access token valid for 1d (based on default module config)
    const accessToken = await this.jwtService.signAsync(payload);
    
    // Refresh token valid for 7d overriding the basic config
    const refreshToken = await this.jwtService.signAsync(payload, {
      secret: this.configService.get<string>('JWT_REFRESH_SECRET'),
      expiresIn: '7d',
    });

    return { accessToken, refreshToken };
  }

  /**
   * Register a new user
   */
  async register(dto: RegisterDto): Promise<AuthTokensResponse> {
    const existingUser = await this.prisma.user.findUnique({
      where: { email: dto.email },
    });

    // Early return
    if (existingUser) {
      throw new BadRequestException('This email is already registered.');
    }

    const hashedPassword = await bcrypt.hash(dto.password, BCRYPT_ROUNDS);

    const newUser = await this.prisma.user.create({
      data: {
        email: dto.email,
        fullName: dto.fullName,     
        password: hashedPassword,
        provider: 'LOCAL',          
        role: 'STUDENT',            
        isActive: true, // Requires email verification / admin activation normally, kept true for flow
      },
    });

    const payload = { sub: newUser.id, email: newUser.email, role: newUser.role };
    const { accessToken, refreshToken } = await this.generateTokens(payload);

    return {
      message: 'Registration successful. Please activate your account to get started.',
      accessToken,
      refreshToken,
      user: {
        id: newUser.id,
        email: newUser.email,
        fullName: newUser.fullName,
        role: newUser.role,
      },
    };
  }

  /**
   * Login user with email and password
   */
  async login(dto: LoginDto): Promise<AuthTokensResponse> {
    const user = await this.prisma.user.findUnique({
      where: { email: dto.email },
    });

    // Early return - Generic error
    if (!user) {
      throw new UnauthorizedException('Incorrect email or password');
    }

    if (user.lockedUntil && user.lockedUntil > new Date()) {
      const remainingTime = Math.ceil((user.lockedUntil.getTime() - Date.now()) / 1000 / 60);
      throw new ForbiddenException(
        `Your account is locked due to too many failed password attempts. Please try again in ${remainingTime} minutes.`
      );
    }

    if (!user.isActive) {
      throw new ForbiddenException(
        'Your account is not activated or has been locked by an administrator. Please contact support.'
      );
    }

    if (!user.password && user.provider !== 'LOCAL') {
      throw new BadRequestException(
        `This account was registered with ${user.provider}. Please sign in using ${user.provider}.`
      );
    }

    const isPasswordValid = await bcrypt.compare(dto.password, user.password || '');

    if (!isPasswordValid) {
      const failedAttempts = user.failedLoginAttempts + 1;
      const updateData: any = { failedLoginAttempts: failedAttempts };

      if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
        updateData.lockedUntil = new Date(Date.now() + ACCOUNT_LOCK_DURATION_MS);
      }

      await this.prisma.user.update({
        where: { id: user.id },
        data: updateData,
      });

      const remainingAttempts = MAX_LOGIN_ATTEMPTS - failedAttempts;
      if (remainingAttempts > 0) {
        throw new UnauthorizedException(
          `Incorrect email or password. ${remainingAttempts} attempts remaining before the account is locked.`
        );
      } else {
        throw new ForbiddenException(
          'Your account has been locked due to too many failed password attempts. Please try again after 15 minutes.'
        );
      }
    }

    if (user.failedLoginAttempts > 0) {
      await this.prisma.user.update({
        where: { id: user.id },
        data: { 
          failedLoginAttempts: 0, 
          lockedUntil: null 
        },
      });
    }

    const payload = { sub: user.id, email: user.email, role: user.role };
    const { accessToken, refreshToken } = await this.generateTokens(payload);

    return {
      message: 'Login successful',
      accessToken,
      refreshToken,
      user: {
        id: user.id,
        email: user.email,
        fullName: user.fullName,
        role: user.role,
      },
    };
  }

  /**
   * Refreshes both Access and Refresh Tokens using a valid refresh token's payload
   * @param userPayload The decoded refresh token payload
   * @returns A fresh token pair
   */
  async refreshTokens(userPayload: JwtRefreshPayload): Promise<AuthTokensResponse> {
    const user = await this.prisma.user.findUnique({
      where: { id: userPayload.sub },
    });

    if (!user) {
      throw new UnauthorizedException('User no longer exists');
    }

    // Verify user is still allowed to hold tokens at all
    if (!user.isActive || (user.lockedUntil && user.lockedUntil > new Date())) {
      throw new ForbiddenException('Account is currently disabled or locked.');
    }

    const payload = { sub: user.id, email: user.email, role: user.role };
    const { accessToken, refreshToken } = await this.generateTokens(payload);

    return {
      message: 'Token refreshed successfully',
      accessToken,
      refreshToken,
      user: {
        id: user.id,
        email: user.email,
        fullName: user.fullName,
        role: user.role,
      },
    };
  }

  /**
   * Get current user details from ID
   */
  async getMe(userId: string): Promise<AuthUserResponse> {
    const user = await this.prisma.user.findUnique({
      where: { id: userId },
      select: {
        id: true,
        email: true,
        fullName: true,
        role: true,
      },
    });

    if (!user) {
      throw new UnauthorizedException('User not found');
    }

    return user;
  }
}
