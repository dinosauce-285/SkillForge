import { ExecutionContext, Injectable } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { AuthGuard } from '@nestjs/passport';
import { IS_PUBLIC_KEY } from '../decorators/public.decorator';
import { Observable } from 'rxjs';

/**
 * JwtAuthGuard
 *
 * Protects routes that require JWT authentication.
 * Automatically validates the Bearer token from the Authorization header.
 *
 * Supports early return bypassing if @Public() decorator is applied to the handler or controller.
 */
@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {
  constructor(private readonly reflector: Reflector) {
    super();
  }

  canActivate(
    context: ExecutionContext,
  ): boolean | Promise<boolean> | Observable<boolean> {
    const isPublic = this.reflector.getAllAndOverride<boolean>(IS_PUBLIC_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    // Early return if endpoint is marked as @Public
    if (isPublic) {
      return true;
    }

    return super.canActivate(context);
  }
}
