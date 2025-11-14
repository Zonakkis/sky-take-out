package com.sky.service.impl;

import com.sky.dto.*;
import com.sky.service.HttpService;
import com.sky.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private HttpService httpService;


    public WxPayJSApiResponse prePay(WxPayJSApiRequest request) {
        WxPayJSApiResponse response = new WxPayJSApiResponse();
        String prepayId = "wx" + System.currentTimeMillis();
        response.setPrepayId(prepayId);
        redisTemplate.opsForValue().set("wx:outTradeNo:prepay:" + prepayId, request.getOutTradeNo(), 2, TimeUnit.HOURS);
        redisTemplate.opsForValue().set("wx:notify:prepay:" + prepayId, request.getNotifyUrl(), 2, TimeUnit.HOURS);
        return response;
    }


    public void payment(WxPayMockDto wxPayDto) throws IOException {
        String prepayId = wxPayDto.getPrepayId();
        if (prepayId == null) {
            return;
        }
        String outTradeNo = (String) redisTemplate.opsForValue().get("wx:outTradeNo:prepay:" + prepayId);
        String notifyUrl = (String) redisTemplate.opsForValue().get("wx:notify:prepay:" + prepayId);
        WxPaySuccessNotifyMockDTO wxPaySuccessNotifyDTO = new WxPaySuccessNotifyMockDTO();
        wxPaySuccessNotifyDTO.setOutTradeNo(outTradeNo);
        if(notifyUrl != null) {
            httpService.post(notifyUrl, wxPaySuccessNotifyDTO, WxPaySuccessNotifyMockDTO.class);
        }
        redisTemplate.delete("wx:prepay:" + prepayId);
        redisTemplate.delete("wx:notify:prepay:" + prepayId);
    }
}
