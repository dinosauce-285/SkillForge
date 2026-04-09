import { Injectable } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

/**
 * JwtRefreshAuthGuard
 * 
 * Protects routes that explicitly require a Refresh Token (e.g., POST /auth/refresh).
 * Validates the Bearer token from the Authorization header using the 'jwt-refresh' passport strategy.
 * This class ensures that normal Access Tokens cannot be used on Refresh endpoints.
 */
@Injectable()
export class JwtRefreshAuthGuard extends AuthGuard('jwt-refresh') {}
