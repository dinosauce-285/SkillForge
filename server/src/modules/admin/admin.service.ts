import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateInstructorDto } from './dto/create-instructor.dto';
import { ModerateCourseDto } from './dto/moderate-course.dto';
import * as bcrypt from 'bcrypt';
import { Role, CourseStatus } from '@prisma/client';

@Injectable()
export class AdminService {
  constructor(private readonly prisma: PrismaService) {}

  async getAllUsers() {
    return this.prisma.user.findMany({
      select: {
        id: true,
        email: true,
        fullName: true,
        role: true,
        isActive: true,
        createdAt: true,
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  async toggleUserBan(id: string) {
    const user = await this.prisma.user.findUnique({ where: { id } });
    if (!user) {
      throw new NotFoundException('User not found');
    }
    if (user.role === Role.ADMIN) {
      throw new BadRequestException('Cannot ban an admin');
    }

    return this.prisma.user.update({
      where: { id },
      data: { isActive: !user.isActive },
      select: {
        id: true,
        email: true,
        isActive: true,
      },
    });
  }

  async createInstructor(dto: CreateInstructorDto) {
    const existing = await this.prisma.user.findUnique({
      where: { email: dto.email },
    });
    if (existing) {
      throw new BadRequestException('Email already in use');
    }

    const defaultPassword = 'Password123!';
    const hashedPassword = await bcrypt.hash(defaultPassword, 10);

    return this.prisma.user.create({
      data: {
        email: dto.email,
        fullName: dto.fullName,
        password: hashedPassword,
        role: Role.INSTRUCTOR,
        isActive: true,
      },
      select: {
        id: true,
        email: true,
        fullName: true,
        role: true,
      },
    });
  }

  async getCourseQueue() {
    return this.prisma.course.findMany({
      where: {
        status: CourseStatus.PENDING,
      },
      include: {
        instructor: {
          select: { id: true, fullName: true, email: true },
        },
        category: true,
      },
      orderBy: { updatedAt: 'asc' },
    });
  }

  async moderateCourse(id: string, dto: ModerateCourseDto) {
    const course = await this.prisma.course.findUnique({ where: { id } });
    if (!course) {
      throw new NotFoundException('Course not found');
    }

    const dataToUpdate: any = { status: dto.status };
    if (dto.level) {
      dataToUpdate.level = dto.level;
    }

    return this.prisma.course.update({
      where: { id },
      data: dataToUpdate,
    });
  }

  async getCoursePreview(id: string) {
    const course = await this.prisma.course.findUnique({
      where: { id },
      include: {
        instructor: { select: { id: true, fullName: true, email: true } },
        category: true,
        chapters: {
          orderBy: { orderIndex: 'asc' },
          include: {
            lessons: {
              orderBy: { orderIndex: 'asc' },
              include: {
                materials: true,
              },
            },
            quizzes: {
              include: {
                questions: {
                  include: {
                    choices: true,
                  },
                },
              },
            },
          },
        },
      },
    });
    if (!course) {
      throw new NotFoundException('Course not found');
    }
    return course;
  }
}
