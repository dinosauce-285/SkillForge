import {
  BadRequestException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { CourseLevel, CourseStatus, Prisma, Role } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { CourseListQueryDto } from './dto/course-list-query.dto';
import { CreateCourseDto } from './dto/create-course.dto';
import { UpdateCourseDto } from './dto/update-course.dto';

@Injectable()
export class CoursesService {
  constructor(private readonly prisma: PrismaService) {}

  async getCourseForManager(id: string, user: { userId: string; role: Role }) {
    const course = await this.prisma.course.findFirst({
      where: {
        id,
        deletedAt: null,
      },
      include: {
        category: true,
        chapters: {
          where: { deletedAt: null },
          orderBy: { orderIndex: 'asc' },
          include: {
            lessons: {
              where: { deletedAt: null },
              orderBy: { orderIndex: 'asc' },
              include: {
                materials: true,
              },
            },
          },
        },
      },
    });

    if (!course) {
      throw new NotFoundException(`Course with id "${id}" not found`);
    }

    this.assertCanManageCourse(course.instructorId, user);

    return course;
  }

  async findAll(query: CourseListQueryDto) {
    const page = this.parsePositiveInt(query.page, 1);
    const limit = this.parsePositiveInt(query.limit, 10);
    const search = query.search?.trim();
    const levelFilter = this.parseLevelFilter(query.level);

    const where: Prisma.CourseWhereInput = {
      status: CourseStatus.PUBLISHED,
      deletedAt: null,
      ...(query.categoryId ? { categoryId: query.categoryId } : {}),
      ...(levelFilter ? { level: levelFilter } : {}),
      ...(search
        ? {
            OR: [
              { title: { contains: search, mode: 'insensitive' } },
              { subtitle: { contains: search, mode: 'insensitive' } },
              { summary: { contains: search, mode: 'insensitive' } },
            ],
          }
        : {}),
    };

    const [total, courses] = await this.prisma.$transaction([
      this.prisma.course.count({ where }),
      this.prisma.course.findMany({
        where,
        skip: (page - 1) * limit,
        take: limit,
        orderBy: { createdAt: 'desc' },
        include: {
          category: true,
          instructor: {
            select: {
              id: true,
              fullName: true,
              profile: {
                select: {
                  avatarUrl: true,
                  skills: true,
                },
              },
            },
          },
          tags: true,
          _count: {
            select: {
              chapters: true,
              enrollments: true,
              reviews: true,
            },
          },
        },
      }),
    ]);

    return {
      data: courses,
      meta: {
        total,
        page,
        limit,
        totalPages: Math.ceil(total / limit),
      },
    };
  }

  async findOne(id: string) {
    const course = await this.prisma.course.findFirst({
      where: {
        id,
        status: CourseStatus.PUBLISHED,
        deletedAt: null,
      },
      include: {
        category: true,
        instructor: {
          select: {
            id: true,
            fullName: true,
            profile: {
              select: {
                avatarUrl: true,
                skills: true,
                learningGoals: true,
              },
            },
          },
        },
        tags: true,
        chapters: {
          where: {
            deletedAt: null,
          },
          orderBy: {
            orderIndex: 'asc',
          },
          include: {
            lessons: {
              where: {
                deletedAt: null,
              },
              orderBy: {
                orderIndex: 'asc',
              },
            },
          },
        },
        _count: {
          select: {
            chapters: true,
            enrollments: true,
            reviews: true,
          },
        },
      },
    });

    if (!course) {
      throw new NotFoundException(`Course with id "${id}" not found`);
    }

    return course;
  }

  async create(user: { userId: string; role: Role }, dto: CreateCourseDto) {
    if (!dto.title?.trim()) {
      throw new BadRequestException('title is required');
    }

    if (!dto.categoryId?.trim()) {
      throw new BadRequestException('categoryId is required');
    }

    const categoryId = dto.categoryId.trim();
    await this.assertCategoryExists(categoryId);

    const price = this.normalizePrice(dto.price);
    const isFree = dto.isFree ?? price === 0;
    const tagIds = this.normalizeTagIds(dto.tagIds);
    await this.assertTagsExist(tagIds);

    const course = await this.prisma.course.create({
      data: {
        instructorId: user.userId,
        categoryId,
        title: dto.title.trim(),
        subtitle: dto.subtitle?.trim() || null,
        summary: dto.summary?.trim() || null,
        thumbnailUrl: dto.thumbnailUrl?.trim() || null,
        promoVideoUrl: dto.promoVideoUrl?.trim() || null,
        price,
        isFree,
        level: this.normalizeLevel(dto.level),
        status: this.normalizeStatus(dto.status),
        tags: tagIds?.length
          ? {
              connect: tagIds.map((id) => ({ id })),
            }
          : undefined,
      },
      include: this.courseDetailInclude(),
    });

    return course;
  }

  async update(
    id: string,
    user: { userId: string; role: Role },
    dto: UpdateCourseDto,
  ) {
    const course = await this.prisma.course.findFirst({
      where: {
        id,
        deletedAt: null,
      },
    });

    if (!course) {
      throw new NotFoundException(`Course with id "${id}" not found`);
    }

    this.assertCanManageCourse(course.instructorId, user);

    if (dto.title !== undefined && !dto.title.trim()) {
      throw new BadRequestException('title cannot be empty');
    }

    if (dto.categoryId !== undefined && !dto.categoryId.trim()) {
      throw new BadRequestException('categoryId cannot be empty');
    }

    const categoryId = dto.categoryId?.trim();

    if (categoryId) {
      await this.assertCategoryExists(categoryId);
    }

    const price =
      dto.price !== undefined ? this.normalizePrice(dto.price) : undefined;
    const isFree =
      dto.isFree ?? (price !== undefined ? price === 0 : undefined);
    const tagIds =
      dto.tagIds !== undefined ? this.normalizeTagIds(dto.tagIds) : undefined;
    await this.assertTagsExist(tagIds);

    const updatedCourse = await this.prisma.course.update({
      where: { id },
      data: {
        title: dto.title?.trim(),
        categoryId,
        subtitle:
          dto.subtitle === undefined ? undefined : dto.subtitle?.trim() || null,
        summary:
          dto.summary === undefined ? undefined : dto.summary?.trim() || null,
        thumbnailUrl:
          dto.thumbnailUrl === undefined
            ? undefined
            : dto.thumbnailUrl?.trim() || null,
        promoVideoUrl:
          dto.promoVideoUrl === undefined
            ? undefined
            : dto.promoVideoUrl?.trim() || null,
        ...(price !== undefined ? { price } : {}),
        ...(isFree !== undefined ? { isFree } : {}),
        level:
          dto.level !== undefined ? this.normalizeLevel(dto.level) : undefined,
        status:
          dto.status !== undefined
            ? this.normalizeStatus(dto.status)
            : undefined,
        ...(tagIds !== undefined
          ? {
              tags: {
                set: tagIds.map((tagId) => ({ id: tagId })),
              },
            }
          : {}),
      },
      include: this.courseDetailInclude(),
    });

    return updatedCourse;
  }

  async remove(id: string, user: { userId: string; role: Role }) {
    const course = await this.prisma.course.findFirst({
      where: {
        id,
        deletedAt: null,
      },
      select: {
        id: true,
        instructorId: true,
      },
    });

    if (!course) {
      throw new NotFoundException(`Course with id "${id}" not found`);
    }

    this.assertCanManageCourse(course.instructorId, user);

    return this.prisma.course.update({
      where: { id },
      data: {
        deletedAt: new Date(),
        status: CourseStatus.DRAFT,
      },
    });
  }

  async findMyCourses(instructorId: string) {
    return this.prisma.course.findMany({
      where: {
        instructorId: instructorId,
        deletedAt: null,
      },
      include: {
        category: true,
        _count: {
          select: { chapters: true, enrollments: true },
        },
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  private parsePositiveInt(
    value: string | number | undefined,
    fallback: number,
  ) {
    if (value === undefined || value === null) {
      return fallback;
    }

    const parsed =
      typeof value === 'number'
        ? Math.trunc(value)
        : Number.parseInt(value, 10);
    return Number.isInteger(parsed) && parsed > 0 ? parsed : fallback;
  }

  private courseDetailInclude() {
    return {
      category: true,
      instructor: {
        select: {
          id: true,
          fullName: true,
          profile: {
            select: {
              avatarUrl: true,
              skills: true,
              learningGoals: true,
            },
          },
        },
      },
      tags: true,
      chapters: {
        where: {
          deletedAt: null,
        },
        orderBy: {
          orderIndex: 'asc' as const,
        },
        include: {
          lessons: {
            where: {
              deletedAt: null,
            },
            orderBy: {
              orderIndex: 'asc' as const,
            },
          },
        },
      },
      _count: {
        select: {
          chapters: true,
          enrollments: true,
          reviews: true,
        },
      },
    };
  }

  private async assertCategoryExists(categoryId: string) {
    const category = await this.prisma.category.findUnique({
      where: { id: categoryId },
      select: { id: true },
    });

    if (!category) {
      throw new BadRequestException(
        `Category with id "${categoryId}" not found`,
      );
    }
  }

  private assertCanManageCourse(
    courseInstructorId: string,
    user: { userId: string; role: Role },
  ) {
    if (user.role === Role.ADMIN) {
      return;
    }

    if (user.role !== Role.INSTRUCTOR || courseInstructorId !== user.userId) {
      throw new ForbiddenException('You are not allowed to manage this course');
    }
  }

  private normalizePrice(value: string | number | undefined) {
    if (value === undefined || value === null) {
      return undefined;
    }

    const parsed = typeof value === 'number' ? value : Number.parseFloat(value);
    if (!Number.isFinite(parsed) || parsed < 0) {
      throw new BadRequestException('price must be a non-negative number');
    }

    return parsed;
  }

  private normalizeLevel(value: CourseLevel | string | undefined) {
    if (!value) {
      return CourseLevel.ALL_LEVELS;
    }

    if (!Object.values(CourseLevel).includes(value as CourseLevel)) {
      throw new BadRequestException(`Invalid level: ${value}`);
    }

    return value as CourseLevel;
  }

  private normalizeStatus(value: CourseStatus | string | undefined) {
    if (!value) {
      return CourseStatus.DRAFT;
    }

    if (!Object.values(CourseStatus).includes(value as CourseStatus)) {
      throw new BadRequestException(`Invalid status: ${value}`);
    }

    return value as CourseStatus;
  }

  private normalizeTagIds(tagIds: string[] | undefined) {
    if (tagIds === undefined) {
      return undefined;
    }

    const normalized = tagIds.map((tagId) => tagId.trim()).filter(Boolean);
    return [...new Set(normalized)];
  }

  private parseLevelFilter(value: CourseLevel | string | undefined) {
    if (!value) {
      return undefined;
    }

    if (!Object.values(CourseLevel).includes(value as CourseLevel)) {
      throw new BadRequestException(`Invalid level: ${value}`);
    }

    return value as CourseLevel;
  }

  private async assertTagsExist(tagIds: string[] | undefined) {
    if (!tagIds || tagIds.length === 0) {
      return;
    }

    const tags = await this.prisma.tag.findMany({
      where: {
        id: {
          in: tagIds,
        },
      },
      select: {
        id: true,
      },
    });

    if (tags.length !== tagIds.length) {
      throw new BadRequestException('One or more tagIds were not found');
    }
  }
}
