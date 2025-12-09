package com.sky.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sky.service.MQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RocketMQProducer implements MQProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /* 发送消息
     * @param topic 主题
     * @param tag 标签
     * @param data 消息内容
     */
    public void send(String topic, String tag, Object data) {
        String json = JSON.toJSONString(data);
        Message<String> message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.send(topic + ":" + tag, message);
    }

    /* 发送延时消息
     * @param topic 主题
     * @param tag 标签
     * @param data 消息内容
     * @param time 延时时间
     * @param timeUnit 时间单位
     */
    public void sendDelay(String topic, String tag, Object data, int time, TimeUnit timeUnit) {
        String json = JSON.toJSONString(data);
        Message<String> message = MessageBuilder.withPayload(json).build();
        long delayTimeMills =  timeUnit.toMillis(time);
        rocketMQTemplate.syncSendDelayTimeMills(topic + ":" + tag, message,delayTimeMills);
    }
}
