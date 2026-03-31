# THÔNG TIN DỰ ÁN SKILLFORGE (NESTJS SERVER)
- **Framework:** NestJS.
- **Ngôn ngữ:** TypeScript.
- **ORM & Database:** Prisma ORM.
- **Kiến trúc:** Feature-based Modular Architecture (Chia module theo tính năng).

## 1. TYPESCRIPT & CLEAN CODE GUIDELINES
- **Ngôn ngữ code:** Sử dụng tiếng Anh cho toàn bộ code, biến, hàm và comment/JSDoc.
- **Typing (Kiểu dữ liệu):** BẮT BUỘC khai báo kiểu cho mọi biến, tham số và giá trị trả về. **Tuyệt đối không dùng `any`.**
- **Naming Conventions (Quy tắc đặt tên):**
  - Tên file/thư mục: `kebab-case` (VD: `courses.controller.ts`, `jwt-auth.guard.ts`).
  - Class: `PascalCase`.
  - Biến/Hàm/Method: `camelCase`. Luôn bắt đầu hàm bằng một động từ (VD: `getCourseById`, `createOrder`).
  - Biến Boolean: Bắt đầu bằng `is`, `has`, `can`, `should` (VD: `isLoading`, `hasPermission`).
  - Hằng số / Environment Variables: `UPPER_SNAKE_CASE`.
- **Functions:** - Ưu tiên viết hàm ngắn, thực hiện một nhiệm vụ duy nhất (Single Responsibility).
  - Tránh nested code (if-else lồng nhau quá sâu) bằng cách sử dụng **Early Return** (Kiểm tra và return sớm).
  - Ưu tiên sử dụng Higher-order functions (`map`, `filter`, `reduce`) thay vì vòng lặp `for` truyền thống.
  - Sử dụng pattern **RO-RO (Request Object - Response Object)**: Gộp nhiều tham số truyền vào thành một Object (DTO) và trả về một Object rõ ràng.

## 2. NESTJS SPECIFIC GUIDELINES
- **Kiến trúc Module:** Khởi tạo API theo từng module riêng biệt đặt trong `src/modules/` (VD: `auth`, `courses`, `users`, `order`).
- **Data Transfer Objects (DTO):** - Mọi request body/query truyền vào Controller ĐỀU PHẢI có DTO đi kèm.
  - Đặt DTO trong thư mục `dto/` của từng module (VD: `modules/courses/dto/create-course.dto.ts`).
  - Bắt buộc sử dụng `@nestjs/swagger`, `class-validator` và `class-transformer` để validate input bên trong DTO.
- **Guards, Decorators & Strategies:** - Tuân thủ tính gắn kết (Cohesion): Đặt các Guards, Decorators, hoặc Strategies vào thẳng bên trong thư mục của Module sở hữu/sử dụng chúng (VD: `jwt-auth.guard.ts` đặt trong `modules/auth/guards/`). Không tự ý tạo thư mục `common` ở ngoài cùng.
- **Prisma & Data Types:**
  - **KHÔNG** tạo thư mục `models/` hay file Entity thủ công. 
  - Sử dụng trực tiếp các Types được sinh ra tự động từ `@prisma/client`.
  - Import và sử dụng `PrismaService` (từ `modules/prisma/prisma.service.ts`) vào các Service khác để tương tác với Database.

## 3. ERROR HANDLING & DATA
- Prefer immutability: Sử dụng `readonly` cho các property trong DTO và các dữ liệu không thay đổi.
- Không thực hiện logic validate dữ liệu thô (như check độ dài chuỗi, định dạng email) bên trong Service. Logic đó phải nằm ở DTO (`class-validator`).
- Sử dụng hệ thống Exception có sẵn của NestJS (VD: `NotFoundException`, `BadRequestException`) để ném lỗi hợp lý ở tầng Service, giúp Controller trả về HTTP Status Code chuẩn xác.