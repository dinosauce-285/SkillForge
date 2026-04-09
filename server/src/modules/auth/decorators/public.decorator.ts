import { SetMetadata } from '@nestjs/common';

export const IS_PUBLIC_KEY = 'isPublic';

/**
 * @Public Decorator
 * 
 * Bypasses the Global AuthGuard for specific endpoints
 * (e.g., login, register)
 */
export const Public = () => SetMetadata(IS_PUBLIC_KEY, true);
