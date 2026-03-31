import {
  BadRequestException,
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service'; // Chỉnh lại đường dẫn nếu cần
import { CreateCategoryDto } from './dto/create-category.dto';
import { UpdateCategoryDto } from './dto/update-category.dto';

@Injectable()
export class CategoriesService {
  constructor(private readonly prisma: PrismaService) {}

  async create(dto: CreateCategoryDto) {
    // Kiểm tra tên danh mục đã tồn tại chưa
    const existingCategory = await this.prisma.category.findUnique({
      where: { name: dto.name.trim() },
    });

    if (existingCategory) {
      throw new ConflictException(`Category with name "${dto.name}" already exists`);
    }

    return this.prisma.category.create({
      data: {
        name: dto.name.trim(),
      },
    });
  }

  async findAll() {
    // Trả về danh sách danh mục, đính kèm luôn số lượng khóa học trong mỗi danh mục
    return this.prisma.category.findMany({
      orderBy: { name: 'asc' },
      include: {
        _count: {
          select: { courses: true },
        },
      },
    });
  }

  async findOne(id: string) {
    const category = await this.prisma.category.findUnique({
      where: { id },
      include: {
        _count: {
          select: { courses: true },
        },
      },
    });

    if (!category) {
      throw new NotFoundException(`Category with ID "${id}" not found`);
    }

    return category;
  }

  async update(id: string, dto: UpdateCategoryDto) {
    await this.findOne(id); // Đảm bảo danh mục tồn tại

    if (dto.name) {
      const existingCategory = await this.prisma.category.findFirst({
        where: { 
          name: dto.name.trim(),
          NOT: { id }, // Bỏ qua chính nó
        },
      });

      if (existingCategory) {
        throw new ConflictException(`Category with name "${dto.name}" already exists`);
      }
    }

    return this.prisma.category.update({
      where: { id },
      data: {
        name: dto.name?.trim(),
      },
    });
  }

  async remove(id: string) {
    const category = await this.findOne(id);

    // Chặn xóa nếu danh mục đang chứa khóa học
    if (category._count.courses > 0) {
      throw new BadRequestException(
        `Cannot delete category "${category.name}" because it contains ${category._count.courses} course(s).`
      );
    }

    return this.prisma.category.delete({
      where: { id },
    });
  }
}