import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateProfileDto } from './dto/update-profile.dto';

@Injectable()
export class UsersService {
    constructor(private prisma: PrismaService) { }

    // translated comment
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
                // translated comment
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
            throw new NotFoundException('User not found');
        }

        return user;
    }

    // translated comment
    async updateProfile(userId: string, dto: UpdateProfileDto) {
        const { fullName, avatarUrl, skills, learningGoals } = dto;

        // translated comment
        const updatedUser = await this.prisma.user.update({
            where: { id: userId },
            data: {
                // translated comment
                ...(fullName && { fullName }),

                // translated comment
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
