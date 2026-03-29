import { Transform } from 'class-transformer';
import { IsString, IsOptional, IsArray, IsUrl, MaxLength, MinLength } from 'class-validator';

export class UpdateProfileDto {
    // Field này thuộc bảng User
    @IsOptional()
    @IsString()
    @MinLength(2, { message: 'Họ tên phải có ít nhất 2 ký tự' })
    @MaxLength(50)
    fullName?: string;  

    @IsOptional()
    @IsUrl({}, { message: 'Link avatar không hợp lệ' })
    avatarUrl?: string;

    @IsOptional()
    @IsArray()
    @IsString({ each: true, message: 'Mỗi skill phải là một chuỗi' })
    skills?: string[];

    @IsOptional()
    @IsString()
    @MaxLength(1000, { message: 'Mục tiêu học tập không được vượt quá 1000 ký tự' })
    // @Transform(({ value }) => DOMPurify.sanitize(value)) // Bật lên nếu dùng Rich Text Editor ở Frontend
    @Transform(({ value }) => value?.trim()) // Xóa khoảng trắng thừa
    learningGoals?: string;   
}