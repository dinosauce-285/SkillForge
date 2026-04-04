import { Controller, Get, Patch, Body, UseGuards } from '@nestjs/common';
import { UsersService } from './users.service';
import { UpdateProfileDto } from './dto/update-profile.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';

@UseGuards(JwtAuthGuard) 
@Controller('users')
export class UsersController {
    constructor(private readonly usersService: UsersService) { }

    @Get('profile')
    getProfile(@CurrentUser('userId') userId: string) {
        // translated comment
        return this.usersService.getProfile(userId);
    }

    @Patch('profile')
    updateProfile(
        @CurrentUser('userId') userId: string,
        @Body() updateProfileDto: UpdateProfileDto,
    ) {
        return this.usersService.updateProfile(userId, updateProfileDto);
    }
}
