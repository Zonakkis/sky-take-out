package com.sky.task;

import com.sky.constant.OrderConstant;
import com.sky.entity.Order;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 每天凌晨0点完成配送中的订单
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void completeDeliveringOrders() {
        log.info("开始执行定时任务，完成配送中的订单");
        List<Order> orders = orderMapper.listByStatus(OrderConstant.Status.DELIVERING);
        for (Order order : orders) {
            order.setStatus(OrderConstant.Status.COMPLETED);
            orderMapper.update(order);
        }
        log.info("定时任务执行完毕，完成配送中的订单数量：{}", orders.size());
    }
}
