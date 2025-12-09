package com.sky.consumer;

import com.alibaba.fastjson2.JSON;
import com.sky.constant.MQTagConstant;
import com.sky.constant.MQTopicConstant;
import com.sky.constant.OrderConstant;
import com.sky.service.OrderService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        consumerGroup = "orderCancelConsumer",
        topic = MQTopicConstant.ORDER,
        selectorExpression = MQTagConstant.CANCEL
)
public class OrderCancelConsumer implements RocketMQListener<String> {
    @Autowired
    private OrderService orderService;

    @Override
    public void onMessage(String string) {
        Long orderId = JSON.parseObject(string, Long.class);
        orderService.cancel(orderId, OrderConstant.CancelReason.TIMEOUT);
    }
}
