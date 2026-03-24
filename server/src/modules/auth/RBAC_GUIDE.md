# RBAC Implementation Guide - SkillForge

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Usage Examples](#usage-examples)
4. [Guard Combinations](#guard-combinations)
5. [Testing RBAC](#testing-rbac)
6. [Troubleshooting](#troubleshooting)

---

## Overview

This guide explains how to use the new Role-Based Access Control (RBAC) system in SkillForge. The system provides three layers of protection:

1. **JwtAuthGuard** - Ensures user is authenticated (has valid JWT)
2. **RolesGuard** - Ensures user has required role(s)
3. **@Roles() Decorator** - Defines which roles are allowed

---

## Architecture

```
┌─────────────────────────────────────┐
│   Incoming Request                  │
│   (with Bearer token)               │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   JwtAuthGuard                      │
│   - Extracts token from header      │
│   - Validates signature & expiry    │
│   - Adds user to request object     │
│   - Returns 401 if invalid          │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   RolesGuard (if @Roles defined)   │
│   - Reads allowed roles from        │
│     @Roles decorator                │
│   - Compares user.role to allowed   │
│   - Returns 403 if not authorized   │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│   Controller Handler Executes       │
│   @Request() req => req.user found  │
└─────────────────────────────────────┘
```

---

## Usage Examples

### Pattern 1: Public Endpoint (No Protection)

```typescript
@Controller('auth')
export class AuthController {
  /**
   * No guards - publicly accessible
   */
  @Post('login')
  login(@Body() loginDto: LoginDto) {
    return this.authService.login(loginDto);
  }

  @Post('register')
  register(@Body() registerDto: RegisterDto) {
    return this.authService.register(registerDto);
  }
}
```

**HTTP Request:**
```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass123"}'
```

**Response:**
```json
{
  "message": "Đăng nhập thành công",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user-id-123",
    "email": "user@example.com",
    "fullName": "John Doe",
    "role": "STUDENT"
  }
}
```

---

### Pattern 2: Authenticated Endpoint (JWT Only)

```typescript
@Controller('users')
export class UsersController {
  constructor(private usersService: UsersService) {}

  /**
   * Any authenticated user can access
   * JWT is required, but role doesn't matter
   */
  @UseGuards(JwtAuthGuard)
  @Get('profile')
  getProfile(@Request() req) {
    // req.user = { userId, email, role }
    return this.usersService.getProfile(req.user.userId);
  }

  /**
   * Another example - getting user's own data
   */
  @UseGuards(JwtAuthGuard)
  @Get('my-enrollments')
  getMyEnrollments(@Request() req) {
    return this.usersService.getEnrollments(req.user.userId);
  }
}
```

**HTTP Request:**
```bash
curl -X GET http://localhost:3000/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Response:**
```json
{
  "id": "user-id-123",
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "STUDENT",
  "createdAt": "2026-03-20T10:30:00Z"
}
```

**Without Token (401 Error):**
```bash
curl -X GET http://localhost:3000/users/profile
# Response: { "message": "Unauthorized", "statusCode": 401 }
```

---

### Pattern 3: Role-Protected Endpoint (JWT + Single Role)

```typescript
@Controller('courses')
export class CoursesController {
  constructor(private coursesService: CoursesService) {}

  /**
   * Only INSTRUCTOR can create courses
   */
  @Roles('INSTRUCTOR')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Post('create')
  createCourse(@Request() req, @Body() createCourseDto: CreateCourseDto) {
    return this.coursesService.create(req.user.userId, createCourseDto);
  }
}
```

**HTTP Request (by INSTRUCTOR):**
```bash
curl -X POST http://localhost:3000/courses/create \
  -H "Authorization: Bearer <instructor-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Advanced TypeScript",
    "description": "Master TypeScript patterns",
    "price": 99.99
  }'
```

**Response (Success - 201):**
```json
{
  "id": "course-id-456",
  "title": "Advanced TypeScript",
  "instructorId": "instructor-id-123",
  "price": 99.99,
  "createdAt": "2026-03-24T12:00:00Z"
}
```

**Request by STUDENT (403 Error):**
```bash
curl -X POST http://localhost:3000/courses/create \
  -H "Authorization: Bearer <student-token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"My Course","price":99.99}'

# Response:
# {
#   "message": "Access Denied. Required roles: INSTRUCTOR. User role: STUDENT",
#   "statusCode": 403
# }
```

---

### Pattern 4: Multi-Role Endpoint (JWT + Multiple Roles)

```typescript
@Controller('admin')
export class AdminController {
  constructor(private adminService: AdminService) {}

  /**
   * Both INSTRUCTOR and ADMIN can access
   * Creates an OR condition: INSTRUCTOR OR ADMIN
   */
  @Roles('INSTRUCTOR', 'ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Get('analytics')
  getAnalytics(@Request() req) {
    // Either INSTRUCTOR or ADMIN can access
    return this.adminService.getAnalytics(req.user.userId, req.user.role);
  }

  /**
   * Only ADMIN can access
   */
  @Roles('ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Delete('users/:id/ban')
  banUser(@Param('id') userId: string) {
    return this.adminService.banUser(userId);
  }

  /**
   * Only ADMIN can access
   */
  @Roles('ADMIN')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Get('all-users')
  getAllUsers() {
    return this.adminService.getAllUsers();
  }
}
```

---

### Pattern 5: Using Request Object in Controller

```typescript
@Controller('orders')
export class OrdersController {
  constructor(private ordersService: OrdersService) {}

  /**
   * Complete example with Request object usage
   */
  @UseGuards(JwtAuthGuard)
  @Post('place-order')
  placeOrder(@Request() req, @Body() orderDto: OrderDto) {
    // req.user contains:
    // - userId: string (user's ID)
    // - email: string (user's email)
    // - role: string ('STUDENT', 'INSTRUCTOR', 'ADMIN')
    
    const userId = req.user.userId;
    const userRole = req.user.role;
    const userEmail = req.user.email;
    
    // Pass to service for processing
    return this.ordersService.placeOrder(userId, orderDto);
  }

  /**
   * Using role in business logic
   */
  @Roles('STUDENT')
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Get('my-orders')
  getMyOrders(@Request() req) {
    // Only STUDENT role reaches here
    return this.ordersService.getOrdersByUserId(req.user.userId);
  }
}
```

---

## Guard Combinations

### Combination 1: JwtAuthGuard Only
```typescript
@UseGuards(JwtAuthGuard)
@Get('endpoint')
handler(@Request() req) {
  // ✅ Requires: Valid JWT token
  // ✅ Allows: Any authenticated user (all roles)
  // ❌ Does NOT check: User's role
}
```

**Access Matrix:**
| Token? | Valid? | Role | Can Access? |
|--------|--------|------|------------|
| No | - | - | ❌ 401 |
| Yes | Invalid | - | ❌ 401 |
| Yes | Valid | STUDENT | ✅ YES |
| Yes | Valid | INSTRUCTOR | ✅ YES |
| Yes | Valid | ADMIN | ✅ YES |

---

### Combination 2: JwtAuthGuard + RolesGuard (No @Roles)
```typescript
@UseGuards(JwtAuthGuard, RolesGuard)
@Get('endpoint')
handler(@Request() req) {
  // ✅ Requires: Valid JWT token
  // ✅ Allows: Any authenticated user (all roles)
  // ❌ Does NOT check: User's role (no restriction)
  // Note: RolesGuard does nothing without @Roles decorator
}
```

**Equivalent to Pattern 1** (JwtAuthGuard only)

---

### Combination 3: JwtAuthGuard + RolesGuard + @Roles
```typescript
@Roles('INSTRUCTOR', 'ADMIN')
@UseGuards(JwtAuthGuard, RolesGuard)
@Get('endpoint')
handler(@Request() req) {
  // ✅ Requires: Valid JWT token
  // ✅ Requires: User role must be INSTRUCTOR or ADMIN
  // ❌ Denies: STUDENT and other roles
}
```

**Access Matrix:**
| Token? | Valid? | Role | Can Access? |
|--------|--------|------|------------|
| No | - | - | ❌ 401 |
| Yes | Invalid | - | ❌ 401 |
| Yes | Valid | STUDENT | ❌ 403 |
| Yes | Valid | INSTRUCTOR | ✅ YES |
| Yes | Valid | ADMIN | ✅ YES |

---

### Combination 4: RolesGuard Only (⚠️ Not Recommended)
```typescript
@Roles('ADMIN')
@UseGuards(RolesGuard)
@Get('endpoint')
handler(@Request() req) {
  // ⚠️ This is INSECURE!
  // JWT is NOT validated, only role is checked
  // Anyone with a JWT payload could access if they modify it
  // ALWAYS pair RolesGuard with JwtAuthGuard
}
```

**❌ DO NOT DO THIS** - Always use both guards together.

---

## Testing RBAC

### 1. Setup - Get Tokens for Each Role

```bash
# Create a STUDENT account
curl -X POST http://localhost:3000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@example.com",
    "fullName": "Student User",
    "password": "Pass123!"
  }'

# Login and get STUDENT token
STUDENT_TOKEN=$(curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","password":"Pass123!"}' \
  | jq -r '.accessToken')

echo "STUDENT_TOKEN=$STUDENT_TOKEN"
```

(Note: You'll need to manually change user role in database for INSTRUCTOR/ADMIN)

```sql
-- In database:
UPDATE users SET role = 'INSTRUCTOR' WHERE email = 'instructor@example.com';
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

### 2. Test: Public Endpoint (No Guard)
```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","password":"Pass123!"}'
# Response: 200 OK ✅
```

### 3. Test: JWT-only Endpoint
```bash
# Without token
curl http://localhost:3000/users/profile
# Response: 401 Unauthorized ✅

# With token
curl -H "Authorization: Bearer $STUDENT_TOKEN" \
  http://localhost:3000/users/profile
# Response: 200 OK ✅
```

### 4. Test: Role-Protected Endpoint
```bash
# STUDENT trying to access INSTRUCTOR endpoint
curl -X POST http://localhost:3000/courses/create \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"My Course","price":99.99}'
# Response: 403 Forbidden ✅

# Get INSTRUCTOR token (after setting role)
INSTRUCTOR_TOKEN=$(...)

# INSTRUCTOR accessing INSTRUCTOR endpoint
curl -X POST http://localhost:3000/courses/create \
  -H "Authorization: Bearer $INSTRUCTOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"My Course","price":99.99}'
# Response: 201 Created ✅
```

---

## Troubleshooting

### Issue 1: "Cannot read property 'user' of undefined"

**Problem:**
```typescript
// ❌ Guard not applied or JWT not validated
@Get('endpoint')
handler(@Request() req) {
  console.log(req.user); // undefined!
}
```

**Solution:**
```typescript
// ✅ Add JwtAuthGuard
@UseGuards(JwtAuthGuard)
@Get('endpoint')
handler(@Request() req) {
  console.log(req.user); // { userId, email, role }
}
```

---

### Issue 2: Getting 403 When Expecting Success

**Problem:**
```typescript
// User token is valid but getting 403
@Roles('INSTRUCTOR')
@UseGuards(JwtAuthGuard, RolesGuard)
@Get('courses')
getCourses() { }
```

**Solution:**
1. Check user's role in database:
```sql
SELECT id, email, role FROM users WHERE id = '...';
```

2. Verify token contains correct role:
```bash
# Decode JWT token
echo $TOKEN | jq -R 'split(".")[1] | @base64d | fromjson'
# Should show: { "sub": "...", "email": "...", "role": "INSTRUCTOR" }
```

3. Verify @Roles decorator is correct:
```typescript
@Roles('INSTRUCTOR') // not @Roles('instructor')
```

---

### Issue 3: "JWT malformed" or "No auth token"

**Problem:**
```bash
# Wrong header format
curl -H "Authorization: eyJhbGciOi..." http://localhost:3000/endpoint
# Response: Unauthorized
```

**Solution:**
```bash
# ✅ Correct format: "Bearer <token>"
curl -H "Authorization: Bearer eyJhbGciOi..." http://localhost:3000/endpoint
```

---

### Issue 4: "Access Denied" with Details

**Problem:**
```
Access Denied. Required roles: INSTRUCTOR, ADMIN. User role: STUDENT
```

**This is correct behavior!** The user doesn't have the required role.

**Solution:** Either:
1. Create an endpoint for STUDENT role
2. Change user's role in database
3. Use public endpoint

---

## Best Practices

### ✅ DO:

```typescript
// 1. Always use both guards for role protection
@Roles('ADMIN')
@UseGuards(JwtAuthGuard, RolesGuard)
@Delete('users/:id')
deleteUser(@Param('id') id: string) { }

// 2. Use JwtAuthGuard for authenticated-only endpoints
@UseGuards(JwtAuthGuard)
@Get('my-profile')
getProfile(@Request() req) { }

// 3. Extract user from request object
@UseGuards(JwtAuthGuard)
@Post('create-order')
createOrder(@Request() req, @Body() dto: OrderDto) {
  const userId = req.user.userId; // Correct
  return this.ordersService.create(userId, dto);
}

// 4. Use meaningful error messages
if (!user.isActive) {
  throw new ForbiddenException('Account not activated. Check your email.');
}

// 5. Implement audit logging for sensitive operations
@Roles('ADMIN')
@UseGuards(JwtAuthGuard, RolesGuard)
@Delete('users/:id')
async deleteUser(@Param('id') id: string, @Request() req) {
  await this.auditLog.log({
    action: 'DELETE_USER',
    actorId: req.user.userId,
    targetId: id,
    timestamp: new Date(),
  });
  return this.usersService.delete(id);
}
```

### ❌ DON'T:

```typescript
// 1. Don't use RolesGuard without JwtAuthGuard
@Roles('ADMIN')
@UseGuards(RolesGuard) // Missing JwtAuthGuard!
@Delete('users/:id')
deleteUser() { }

// 2. Don't hardcode user IDs - use req.user
@Get('profile')
getProfile() {
  return this.usersService.getProfile('user-id-123'); // Might be wrong!
}

// 3. Don't forget case sensitivity
@Roles('instructor') // Wrong - should be 'INSTRUCTOR'
@UseGuards(JwtAuthGuard, RolesGuard)
@Post('course')
createCourse() { }

// 4. Don't expose sensitive info in error messages
throw new ForbiddenException(`User ${userId} not found in database`);

// 5. Don't skip authentication checks
@Post('sensitive-operation')
sensitiveOp() { // No guards!
  // Anyone can call this
}
```

---

## Summary

| Scenario | Use Guards | Use @Roles | Example |
|----------|-----------|-----------|---------|
| Public endpoint | ❌ No | ❌ No | Login, Register |
| User profile (any auth user) | ✅ JwtAuthGuard | ❌ No | Get my profile |
| Instructor courses | ✅ Both | ✅ @Roles('INSTRUCTOR') | Create course |
| Admin panel | ✅ Both | ✅ @Roles('ADMIN') | Ban user, View reports |
| Multi-role endpoint | ✅ Both | ✅ @Roles('INSTRUCTOR','ADMIN') | View analytics |

---

## Quick Reference

```typescript
// Imports needed in controller
import { UseGuards, Request } from '@nestjs/common';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';

// Pattern templates
@UseGuards(JwtAuthGuard)
@Get() public_endpoint(@Request() req) { }

@Roles('INSTRUCTOR')
@UseGuards(JwtAuthGuard, RolesGuard)
@Post() instructor_only(@Request() req) { }

@Roles('ADMIN')
@UseGuards(JwtAuthGuard, RolesGuard)
@Delete(':id') admin_only(@Param('id') id: string) { }
```

---

**Last Updated:** March 24, 2026  
**Maintained By:** SkillForge Backend Team
