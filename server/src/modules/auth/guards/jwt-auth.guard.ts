import { Injectable } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

/**
 * JwtAuthGuard
 * 
 * Protects routes that require JWT authentication.
 * Automatically validates the Bearer token from the Authorization header.
 * 
 * Usage:
 * @UseGuards(JwtAuthGuard)
 * @Get('profile')
 * getProfile(@Request() req) {
 *   return req.user; // Contains { userId, email, role }
 * }
 */
@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {}
