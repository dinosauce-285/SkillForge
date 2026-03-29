import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateProfileDto } from './dto/update-profile.dto';

@Injectable()
export class UsersService {
    constructor(private prisma: PrismaService) { }

    // 1. Lấy thông tin Profile
    async getProfile(userId: string) {
        const user = await this.prisma.user.findUnique({
            where: { id: userId },
            select: {
                id: true,
                email: true,
                fullName: true,
                role: true,
                provider: true,
                isActive: true,
                createdAt: true,
                // Nối (Join) với bảng profile
                profile: {
                    select: {
                        avatarUrl: true,
                        skills: true,
                        learningGoals: true,
                    },
                },
            },
        });

        if (!user) {
            throw new NotFoundException('Không tìm thấy người dùng');
        }

        return user;
    }

    // 2. Cập nhật Profile
    async updateProfile(userId: string, dto: UpdateProfileDto) {
        const { fullName, avatarUrl, skills, learningGoals } = dto;

        // Prisma cho phép update bảng User và upsert bảng UserProfile trong cùng 1 câu lệnh (Transaction ngầm)
        const updatedUser = await this.prisma.user.update({
            where: { id: userId },
            data: {
                // Cập nhật bảng User
                ...(fullName && { fullName }),

                // Cập nhật hoặc tạo mới bảng UserProfile
                profile: {
                    upsert: {
                        create: {
                            avatarUrl,
                            skills: skills || [],
                            learningGoals,
                        },
                        update: {
                            ...(avatarUrl && { avatarUrl }),
                            ...(skills && { skills }),
                            ...(learningGoals && { learningGoals }),
                        },
                    },
                },
            },
            select: {
                id: true,
                fullName: true,
                profile: true,
            },
        });

        return updatedUser;
    }
}