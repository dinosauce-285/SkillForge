# SkillForge Auth - Quick Reference Card

## 1. Import What You Need

```typescript
import { UseGuards, Request } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
```

## 2. Choose Your Pattern

### Public - No Protection
```typescript
@Post('login')
login(@Body() dto: LoginDto) {
  // Anyone can access
  return this.authService.login(dto);
}
```

### Authenticated Users Only
```typescript
@UseGuards(JwtAuthGuard)
@Get('profile')
getProfile(@Request() req) {
  // req.user = { userId, email, role }
  return this.usersService.getProfile(req.user.userId);
}
```

### Specific Roles Only
```typescript
@Roles('INSTRUCTOR')
@UseGuards(JwtAuthGuard, RolesGuard)
@Post('create-course')
createCourse(@Request() req, @Body() dto: CreateCourseDto) {
  // Only INSTRUCTOR can access
  return this.coursesService.create(req.user.userId, dto);
}
```

### Multiple Roles
```typescript
@Roles('INSTRUCTOR', 'ADMIN')
@UseGuards(JwtAuthGuard, RolesGuard)
@Get('analytics')
getAnalytics(@Request() req) {
  // INSTRUCTOR OR ADMIN can access
  return this.analyticsService.get(req.user.role);
}
```

## 3. Available Roles
- `STUDENT`
- `INSTRUCTOR`
- `ADMIN`

## 4. Request User Object
```typescript
req.user = {
  userId: string;   // User's ID
  email: string;    // User's email
  role: string;     // One of: STUDENT, INSTRUCTOR, ADMIN
}
```

## 5. Error Codes

| Code | Guard | Reason |
|------|-------|--------|
| 401 | JwtAuthGuard | No token / Invalid token / Token expired |
| 403 | RolesGuard | User role not in @Roles() |
| 403 | Service | Other business logic (account locked, etc) |

## 6. Usage Checklist

- [ ] Do I need JwtAuthGuard? (Is auth required?)
  - YES → Add `@UseGuards(JwtAuthGuard)`
  - NO → Skip (public endpoint)

- [ ] Do I need to check roles? (Is this role-specific?)
  - YES → Add `@Roles('ROLE1', 'ROLE2')` + `RolesGuard`
  - NO → Just use JwtAuthGuard

- [ ] Do I need request.user?
  - YES → Add `@Request() req` parameter
  - NO → Skip

## 7. Common Mistakes
```typescript
// ❌ Missing both decorators
@Get('admin')
handler() { } // INSECURE - no protection!

// ❌ Only using RolesGuard
@Roles('ADMIN')
@UseGuards(RolesGuard) // Missing JwtAuthGuard!

// ❌ Wrong role case
@Roles('admin') // Should be 'ADMIN'

// ❌ Not using req.user
@UseGuards(JwtAuthGuard)
@Get('profile')
handler() {
  return hardcodedUserId; // Wrong!
}

// ✅ Correct
@UseGuards(JwtAuthGuard)
@Get('profile')
handler(@Request() req) {
  return this.users.getProfile(req.user.userId); // Correct!
}
```

## 8. Example: Complete Module

```typescript
import { Controller, Get, Post, UseGuards, Request } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { CoursesService } from './courses.service';

@Controller('courses')
export class CoursesController {
  constructor(private coursesService: CoursesService) {}

  // Public - anyone can browse
  @Get('browse')
  browseCourses() {
    return this.coursesService.getPublished();
  }

  // Authenticated only
  @UseGuards(JwtAuthGuard)
  @Post('enroll')
  enrollCourse(@Request() req, @Body() dto: EnrollDto) {
    return this.coursesService.enroll(req.user.userId, dto);
  }

  // Instructor only
  @Roles('INSTRUCTOR')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Post('create')
  createCourse(@Request() req, @Body() dto: CreateCourseDto) {
    return this.coursesService.create(req.user.userId, dto);
  }

  // Admin only
  @Roles('ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Post(':id/publish')
  publishCourse(@Param('id') courseId: string) {
    return this.coursesService.publish(courseId);
  }
}
```

## 9. Testing with cURL

```bash
# Get token
TOKEN=$(curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass123"}' \
  | jq -r '.accessToken')

# Without token (401)
curl http://localhost:3000/courses/enroll

# With token (200)
curl -H "Authorization: Bearer $TOKEN" http://localhost:3000/courses/enroll

# Wrong role (403)
curl -H "Authorization: Bearer $TOKEN" \
  -X POST http://localhost:3000/courses/create
```

## 10. Need Help?

See:
- Full guide: [RBAC_GUIDE.md](./RBAC_GUIDE.md)
- Code review: [CODE_REVIEW.md](./CODE_REVIEW.md)
- Source: [guards/](./guards/), [decorators/](./decorators/), [strategies/](./strategies/)
