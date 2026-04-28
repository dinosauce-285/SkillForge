import { Module } from "@nestjs/common";
import { ReviewsService } from "./reviews.service";
import { ReviewsController } from "./reviews.controller";
import { PrismaModule } from "../prisma/prisma.module";
import { ProgressModule } from "../progress/progress.module";

@Module({
    imports: [PrismaModule, ProgressModule],
    controllers: [ReviewsController],
    providers: [ReviewsService],
    exports: [ReviewsService],
})
export class ReviewModule { }
