import {
    Injectable,
    NotFoundException,
    InternalServerErrorException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateProfileDto } from './dto/update-profile.dto';
import { SupabaseService } from '../../supabase/supabase.service';

@Injectable()
export class UsersService {
    constructor(
        private readonly prisma: PrismaService,
        private readonly supabaseService: SupabaseService,
    ) {}
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

    async updateProfile(userId: string, dto: UpdateProfileDto) {
        const { fullName, avatarUrl, skills, learningGoals } = dto;

        const updatedUser = await this.prisma.user.update({
            where: { id: userId },
            data: {
                ...(fullName && { fullName }),

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

    async uploadAvatar(
        userId: string,
        avatarFile: Express.Multer.File,
    ): Promise<{ url: string }> {
        const fileExt = avatarFile.originalname.split('.').pop();
        const uniqueFileName = `${userId}-${Date.now()}.${fileExt}`;

        const supabase = this.supabaseService.getClient();

        const { error } = await supabase.storage
            .from('avatars')
            .upload(uniqueFileName, avatarFile.buffer, {
                contentType: avatarFile.mimetype,
                upsert: false,
            });

        if (error) {
            console.error('Supabase upload error:', error);
            throw new InternalServerErrorException('Failed to upload avatar image');
        }

        const { data: publicUrlData } = supabase.storage
            .from('avatars')
            .getPublicUrl(uniqueFileName);

        const avatarUrl = publicUrlData.publicUrl;

        await this.prisma.user.update({
            where: { id: userId },
            data: {
                profile: {
                    upsert: {
                        create: {
                            avatarUrl,
                            skills: [],
                        },
                        update: {
                            avatarUrl,
                        },
                    },
                },
            },
        });

        return { url: avatarUrl };
    }
}
