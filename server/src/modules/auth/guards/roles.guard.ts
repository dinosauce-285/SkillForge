import { Injectable, CanActivate, ExecutionContext, ForbiddenException } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { ROLES_KEY } from '../decorators/roles.decorator';

/**
 * RolesGuard
 * 
 * Checks if the authenticated user's role matches one of the allowed roles
 * defined by the @Roles() decorator on the endpoint.
 * 
 * Must be used TOGETHER with JwtAuthGuard for full protection:
 * @UseGuards(JwtAuthGuard, RolesGuard)
 * 
 * If no @Roles() decorator is present, the guard allows access (acts as authentication-only).
 */
@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    // Get the allowed roles from the @Roles() decorator
    const requiredRoles = this.reflector.getAllAndOverride<string[]>(ROLES_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    // If no roles are specified, allow access (only JWT auth is required)
    if (!requiredRoles) {
      return true;
    }

    // Extract the request object and authenticated user
    const request = context.switchToHttp().getRequest();
    const user = request.user;

    // Verify user exists (should be guaranteed by JwtAuthGuard)
    if (!user) {
      throw new ForbiddenException('User not authenticated');
    }

    // Check if user's role is in the allowed roles
    const hasRole = requiredRoles.includes(user.role);

    if (!hasRole) {
      throw new ForbiddenException(
        `Access Denied. Required roles: ${requiredRoles.join(', ')}. User role: ${user.role}`,
      );
    }

    return true;
  }
}
