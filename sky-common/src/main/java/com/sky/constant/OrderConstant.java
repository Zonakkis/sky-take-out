package com.sky.constant;

public class OrderConstant {
    public static class Status {
        // 1 待付款
        public static final Integer PENDING_PAYMENT = 1;
        // 2 待接单
        public static final Integer TO_BE_CONFIRMED = 2;
        // 3 已接单
        public static final Integer CONFIRMED = 3;
        // 4 派送中
        public static final Integer DELIVERING = 4;
        // 5 已完成
        public static final Integer COMPLETED = 5;
        // 6 已取消
        public static final Integer CANCELLED = 6;
    }

    public static class PayStatus {
        // 0 未支付
        public static final Integer UNPAID = 0;
        // 1 已支付
        public static final Integer PAID = 1;
        // 2 退款
        public static final Integer REFUND = 2;
    }

    public static class CancelReason {
        public static final String USER_CANCELLED = "用户取消订单";
        public static final String TIMEOUT = "订单超时未支付";
    }
}
