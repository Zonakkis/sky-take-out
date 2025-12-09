package com.sky.service;

import java.util.concurrent.TimeUnit;

public interface MQProducer {

    void send(String topic, String tag, Object data);

    void sendDelay(String topic, String tag, Object data, int time, TimeUnit timeUnit);
}
