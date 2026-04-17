import { SetMetadata } from '@nestjs/common';

export const ROLES_KEY = 'roles';

/**
 * @Roles()
 *
 * Custom decorator to define allowed roles for a specific endpoint.
 * Must be used in conjunction with RolesGuard.
 *
 * Usage:
 * @Roles('INSTRUCTOR', 'ADMIN')
 * @UseGuards(JwtAuthGuard, RolesGuard)
 * @Post('create-course')
 * createCourse(@Body() dto: CreateCourseDto) {
 *   // Only INSTRUCTOR and ADMIN can access this
 * }
 */
export const Roles = (...roles: string[]) => SetMetadata(ROLES_KEY, roles);
