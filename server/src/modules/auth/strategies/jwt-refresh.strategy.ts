import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { ConfigService } from '@nestjs/config';
import { Request } from 'express';

// Defines the strongly-typed payload structure expected in the JWT token.
// Strictly avoids using 'any'
export interface JwtRefreshPayload {
  readonly sub: string;
  readonly email: string;
  readonly role: string;
  readonly iat: number;
  readonly exp: number;
}

@Injectable()
export class JwtRefreshStrategy extends PassportStrategy(Strategy, 'jwt-refresh') {
  constructor(private readonly configService: ConfigService) {
    const jwtRefreshSecret = configService.get<string>('JWT_REFRESH_SECRET');
    
    if (!jwtRefreshSecret) {
      throw new Error('JWT_REFRESH_SECRET is not defined in the environment variables');
    }

    super({
      // Extracts the string after "Bearer " from the Authorization header
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: jwtRefreshSecret,
      // Passes the original request to the validate() method to allow further headers inspection
      passReqToCallback: true,
    });
  }

  /**
   * Validates the decoded refresh token.
   * Passport automatically verifies the signature and expiration before calling this.
   * 
   * @param request - The raw Express Request object
   * @param payload - The strictly typed decoded JWT payload
   * @returns The payload to be seamlessly attached to `request.user`
   * @throws UnauthorizedException if extraction strangely fails
   */
  async validate(request: Request, payload: JwtRefreshPayload): Promise<JwtRefreshPayload> {
    const authHeader = request.headers.authorization;
    const refreshToken = authHeader?.replace('Bearer', '').trim();
    
    if (!refreshToken) {
      throw new UnauthorizedException('Refresh token is missing or malformed');
    }

    // In a fully enterprise setting, we would check the raw refreshToken against the DB or Redis.
    // For now, ensuring strong signature and expiration validation is sufficient.
    
    return payload; // Return explicitly typed payload instead of 'any'
  }
}
