package com.sky.service;

import com.sky.dto.WxPayDto;
import com.sky.dto.WxPayMockDto;

import java.math.BigDecimal;

public interface WeChatService {
    WxPayMockDto pay(String orderNum, BigDecimal total, String description, String openid) throws Exception;

    String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) throws Exception;
}
