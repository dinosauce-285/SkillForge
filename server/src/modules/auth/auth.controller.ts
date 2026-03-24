import { Body, Controller, Post, UseGuards, Request, Get } from '@nestjs/common';
import { AuthService } from './auth.service';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';
import { JwtAuthGuard } from './guards/jwt-auth.guard';
import { RolesGuard } from './guards/roles.guard';
import { Roles } from './decorators/roles.decorator';

@Controller('auth') 
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  /**
   * Public endpoint - No authentication required
   */
  @Post('register')
  register(@Body() registerDto: RegisterDto) {
    console.log('Received registration data:', registerDto);
    return this.authService.register(registerDto);
  }

  /**
   * Public endpoint - No authentication required
   */
  @Post('login')
  login(@Body() loginDto: LoginDto) {
    return this.authService.login(loginDto);
  }

  /**
   * Protected endpoint - Requires JWT authentication only
   * Any authenticated user can access this
   */
  @UseGuards(JwtAuthGuard)
  @Get('profile')
  getProfile(@Request() req) {
    return {
      message: 'Profile retrieved successfully',
      user: req.user, // { userId, email, role }
    };
  }

  /**
   * Protected endpoint - Requires JWT + INSTRUCTOR or ADMIN role
   * Usage: Use this pattern for endpoints that need role-based access
   */
  @Roles('INSTRUCTOR', 'ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Get('instructor-dashboard')
  getInstructorDashboard(@Request() req) {
    return {
      message: 'Instructor dashboard data',
      instructorId: req.user.userId,
      role: req.user.role,
    };
  }

  /**
   * Protected endpoint - Requires JWT + ADMIN role only
   */
  @Roles('ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Get('admin-panel')
  getAdminPanel(@Request() req) {
    return {
      message: 'Admin panel data',
      adminId: req.user.userId,
      role: req.user.role,
    };
  }
}