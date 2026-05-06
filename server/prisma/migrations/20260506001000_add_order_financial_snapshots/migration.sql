-- CreateTable
CREATE TABLE "order_financial_snapshots" (
    "id" TEXT NOT NULL,
    "order_id" TEXT NOT NULL,
    "original_course_price" DECIMAL(10,2) NOT NULL,
    "customer_paid_amount" DECIMAL(10,2) NOT NULL,
    "coupon_id" TEXT,
    "coupon_code" TEXT,
    "coupon_scope" "CouponScope",
    "discount_amount" DECIMAL(10,2) NOT NULL,
    "discount_absorbed_by_platform" DECIMAL(10,2) NOT NULL,
    "discount_absorbed_by_instructor" DECIMAL(10,2) NOT NULL,
    "platform_share_rate" INTEGER NOT NULL DEFAULT 30,
    "instructor_share_rate" INTEGER NOT NULL DEFAULT 70,
    "instructor_gross_revenue" DECIMAL(10,2) NOT NULL,
    "instructor_net_revenue" DECIMAL(10,2) NOT NULL,
    "platform_gross_revenue" DECIMAL(10,2) NOT NULL,
    "platform_net_revenue" DECIMAL(10,2) NOT NULL,
    "pending_release_date" TIMESTAMP(3) NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "order_financial_snapshots_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "order_financial_snapshots_order_id_key" ON "order_financial_snapshots"("order_id");

-- CreateIndex
CREATE INDEX "order_financial_snapshots_created_at_idx" ON "order_financial_snapshots"("created_at");

-- CreateIndex
CREATE INDEX "order_financial_snapshots_pending_release_date_idx" ON "order_financial_snapshots"("pending_release_date");

-- CreateIndex
CREATE INDEX "order_financial_snapshots_coupon_scope_idx" ON "order_financial_snapshots"("coupon_scope");

-- AddForeignKey
ALTER TABLE "order_financial_snapshots" ADD CONSTRAINT "order_financial_snapshots_order_id_fkey" FOREIGN KEY ("order_id") REFERENCES "orders"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "order_financial_snapshots" ADD CONSTRAINT "order_financial_snapshots_coupon_id_fkey" FOREIGN KEY ("coupon_id") REFERENCES "coupons"("id") ON DELETE SET NULL ON UPDATE CASCADE;
