-- CreateEnum
CREATE TYPE "InstructorSubscriptionStatus" AS ENUM ('ACTIVE', 'EXPIRED', 'CANCELLED');

-- CreateEnum
CREATE TYPE "InstructorSubscriptionPaymentStatus" AS ENUM ('PENDING', 'SUCCEEDED', 'FAILED');

-- CreateTable
CREATE TABLE "instructor_subscriptions" (
    "id" TEXT NOT NULL,
    "user_id" TEXT NOT NULL,
    "plan_code" TEXT NOT NULL,
    "amount" DECIMAL(10,2) NOT NULL,
    "currency" TEXT NOT NULL DEFAULT 'USD',
    "status" "InstructorSubscriptionStatus" NOT NULL DEFAULT 'ACTIVE',
    "payment_status" "InstructorSubscriptionPaymentStatus" NOT NULL DEFAULT 'SUCCEEDED',
    "mock_payment_reference" TEXT NOT NULL,
    "started_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "expires_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "instructor_subscriptions_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "instructor_subscriptions_user_id_key" ON "instructor_subscriptions"("user_id");

-- CreateIndex
CREATE UNIQUE INDEX "instructor_subscriptions_mock_payment_reference_key" ON "instructor_subscriptions"("mock_payment_reference");

-- CreateIndex
CREATE INDEX "instructor_subscriptions_status_idx" ON "instructor_subscriptions"("status");

-- AddForeignKey
ALTER TABLE "instructor_subscriptions" ADD CONSTRAINT "instructor_subscriptions_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;
