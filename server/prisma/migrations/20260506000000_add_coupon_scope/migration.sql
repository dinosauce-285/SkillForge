-- CreateEnum
CREATE TYPE "CouponScope" AS ENUM ('INSTRUCTOR', 'PLATFORM');

-- AlterTable
ALTER TABLE "coupons" ADD COLUMN "scope" "CouponScope" NOT NULL DEFAULT 'INSTRUCTOR',
ALTER COLUMN "instructor_id" DROP NOT NULL;

-- CreateIndex
CREATE INDEX "coupons_scope_idx" ON "coupons"("scope");
