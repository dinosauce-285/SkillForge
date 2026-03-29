import { createParamDecorator, ExecutionContext } from '@nestjs/common';

export const CurrentUser = createParamDecorator(
    (data: string | undefined, ctx: ExecutionContext) => {
        const request = ctx.switchToHttp().getRequest();
        const user = request.user;

        // Nếu truyền data (ví dụ: @CurrentUser('userId')), chỉ trả về field đó
        // Nếu không truyền, trả về toàn bộ object user từ token
        return data ? user?.[data] : user;
    },
);