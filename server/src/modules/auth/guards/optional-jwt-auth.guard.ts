import { ExecutionContext, Injectable } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class OptionalJwtAuthGuard extends AuthGuard('jwt') {
  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request = context.switchToHttp().getRequest();
    const authorization = request.headers?.authorization;

    if (!authorization) {
      return true;
    }

    try {
      await super.canActivate(context);
    } catch {
      request.user = null;
    }

    return true;
  }

  handleRequest(_err: any, user: any) {
    return user ?? null;
  }
}
