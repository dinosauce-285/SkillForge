import { BadRequestException, Injectable, UnauthorizedException, ForbiddenException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { PrismaService } from '../prisma/prisma.service';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';
import * as bcrypt from 'bcrypt';

/**
 * Security Configuration Constants
 */
const MAX_LOGIN_ATTEMPTS = 5; // Lock account after 5 failed attempts
const ACCOUNT_LOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutes
const BCRYPT_ROUNDS = 10;

@Injectable()
export class AuthService {
  constructor(
    private prisma: PrismaService,
    private jwtService: JwtService,
  ) {}

  /**
   * Register a new user
   * - Only allows registration with email and password (LOCAL provider)
   * - New users are created with STUDENT role and require activation
   * - Passwords are hashed using bcrypt
   * 
   * @param dto - RegisterDto containing email, fullName, password
   * @returns Created user object (without password)
   */
  async register(dto: RegisterDto) {
    // Check if email already exists
    const existingUser = await this.prisma.user.findUnique({
      where: { email: dto.email },
    });

    if (existingUser) {
      throw new BadRequestException('This email is already registered.');
    }

    // Hash password with bcrypt
    const hashedPassword = await bcrypt.hash(dto.password, BCRYPT_ROUNDS);

    // Create new user with default STUDENT role
    const newUser = await this.prisma.user.create({
      data: {
        email: dto.email,
        fullName: dto.fullName,     
        password: hashedPassword,
        provider: 'LOCAL',          
        role: 'STUDENT',            
        isActive: true, // Requires email verification / admin activation
      },
      // Exclude password from response
      select: {
        id: true,
        email: true,
        fullName: true,
        role: true,
        provider: true,
        isActive: true,
        createdAt: true,
      },
    });

    return {
      message: 'Registration successful. Please activate your account to get started.',
      user: newUser,
    };
  }

  /**
   * Login user with email and password
   * - Validates email and password
   * - Implements account locking after MAX_LOGIN_ATTEMPTS failed attempts
   * - Prevents login if account is not active
   * - Returns JWT access token on successful login
   * 
   * @param dto - LoginDto containing email and password
   * @returns JWT access token and user info
   * @throws UnauthorizedException for invalid credentials
   * @throws ForbiddenException for locked/inactive accounts
   */
  async login(dto: LoginDto) {
    // Find user by email
    const user = await this.prisma.user.findUnique({
      where: { email: dto.email },
    });

    // User not found (generic error to prevent email enumeration)
    if (!user) {
      throw new UnauthorizedException('Incorrect email or password');
    }

    // Check if account is currently locked due to failed attempts
    if (user.lockedUntil && user.lockedUntil > new Date()) {
      const remainingTime = Math.ceil((user.lockedUntil.getTime() - Date.now()) / 1000 / 60);
      throw new ForbiddenException(
        `Your account is locked due to too many failed password attempts. Please try again in ${remainingTime} minutes.`
      );
    }

    // Check if account is active
    if (!user.isActive) {
      throw new ForbiddenException(
        'Your account is not activated or has been locked by an administrator. Please contact support.'
      );
    }

    // Validate provider: if user created via OAuth, they can't login with password
    if (!user.password && user.provider !== 'LOCAL') {
      throw new BadRequestException(
        `This account was registered with ${user.provider}. Please sign in using ${user.provider}.`
      );
    }

    // Verify password using bcrypt
    const isPasswordValid = await bcrypt.compare(dto.password, user.password || '');

    if (!isPasswordValid) {
      // Increment failed login attempts and potentially lock account
      const failedAttempts = user.failedLoginAttempts + 1;
      const updateData: any = { failedLoginAttempts: failedAttempts };

      // Lock account if max attempts reached
      if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
        updateData.lockedUntil = new Date(Date.now() + ACCOUNT_LOCK_DURATION_MS);
      }

      await this.prisma.user.update({
        where: { id: user.id },
        data: updateData,
      });

      // Determine error message based on current attempt count
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

    // Reset failed login attempts on successful login
    if (user.failedLoginAttempts > 0) {
      await this.prisma.user.update({
        where: { id: user.id },
        data: { 
          failedLoginAttempts: 0, 
          lockedUntil: null 
        },
      });
    }

    // Generate JWT token
    const payload = { sub: user.id, email: user.email, role: user.role };
    const accessToken = await this.jwtService.signAsync(payload);

    return {
      message: 'Login successful',
      accessToken,
      user: {
        id: user.id,
        email: user.email,
        fullName: user.fullName,
        role: user.role,
      },
    };
  }
}
